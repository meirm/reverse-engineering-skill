# Concepts — pytest-asyncio 0.1.dev1 documentation

## asyncio event loops

In order to understand how pytest-asyncio works, it helps to understand how pytest collectors work.
If you already know about pytest collectors, please skip ahead.
Otherwise, continue reading.
Let's assume we have a test suite with a file named _test_all_the_things.py_ holding a single test, async or not:

```python
import asyncio

import pytest

@pytest.mark.asyncio
async def test_runs_in_a_loop():
    assert asyncio.get_running_loop()
```

The file _test_all_the_things.py_ is a Python module with a Python test function.
When we run pytest, the test runner descends into Python packages, modules, and classes, in order to find all tests, regardless whether the tests will run or not.
This process is referred to as _test collection_ by pytest.
In our particular example, pytest will find our test module and the test function.
We can visualize the collection result by running `pytest --collect-only`:

```
<Module test_all_the_things.py>
  <Function test_runs_in_a_loop>
```

The example illustrates that the code of our test suite is hierarchical.
Pytest uses so called _collectors_ for each level of the hierarchy.
Our contrived example test suite uses the _Module_ and _Function_ collectors, but real world test code may contain additional hierarchy levels via the _Package_ or _Class_ collectors.
There's also a special _Session_ collector at the root of the hierarchy.
You may notice that the individual levels resemble the possible scopes of a pytest fixture.

Pytest-asyncio provides one asyncio event loop for each pytest collector.
By default, each test runs in the event loop provided by the _Function_ collector, i.e. tests use the loop with the narrowest scope.
This gives the highest level of isolation between tests.
If two or more tests share a common ancestor collector, the tests can be configured to run in their ancestor's loop by passing the appropriate _loop_scope_ keyword argument to the _asyncio_ mark.
For example, the following two tests use the asyncio event loop provided by the _Module_ collector:

```python
import asyncio

import pytest

loop: asyncio.AbstractEventLoop

@pytest.mark.asyncio(loop_scope="module")
async def test_remember_loop():
    global loop
    loop = asyncio.get_running_loop()

@pytest.mark.asyncio(loop_scope="module")
async def test_runs_in_a_loop():
    global loop
    assert asyncio.get_running_loop() is loop
```

It's highly recommended for neighboring tests to use the same event loop scope.
For example, all tests in a class or module should use the same scope.
Assigning neighboring tests to different event loop scopes is discouraged as it can make test code hard to follow.

## Test discovery modes

Pytest-asyncio provides two modes for test discovery, _strict_ and _auto_. This can be set through Pytest's `--asyncio-mode` command line flag, or through the configuration file:

```ini
[tool.pytest.ini_options]
asyncio_mode = "auto" # or "strict"
```

### Strict mode

In strict mode pytest-asyncio will only run tests that have the _asyncio_ marker and will only evaluate async fixtures decorated with `@pytest_asyncio.fixture`. Test functions and fixtures without these markers and decorators will not be handled by pytest-asyncio.

This mode is intended for projects that want so support multiple asynchronous programming libraries as it allows pytest-asyncio to coexist with other async testing plugins in the same codebase.

Pytest automatically enables installed plugins. As a result pytest plugins need to coexist peacefully in their default configuration. This is why strict mode is the default mode.

### Auto mode

In _auto_ mode pytest-asyncio automatically adds the _asyncio_ marker to all asynchronous test functions. It will also take ownership of all async fixtures, regardless of whether they are decorated with `@pytest.fixture` or `@pytest_asyncio.fixture`.

This mode is intended for projects that use _asyncio_ as their only asynchronous programming library. Auto mode makes for the simplest test and fixture configuration and is the recommended default.

If you intend to support multiple asynchronous programming libraries, e.g. _asyncio_ and _trio_, strict mode will be the preferred option.

## Test execution and concurrency

pytest-asyncio runs async tests sequentially, just like how pytest runs synchronous tests. Each asynchronous test runs within its assigned event loop. For example, consider the following two tests:

```python
import asyncio

import pytest

@pytest.mark.asyncio
async def test_first():
    await asyncio.sleep(2)  # Takes 2 seconds

@pytest.mark.asyncio
async def test_second():
    await asyncio.sleep(2)  # Takes 2 seconds

# Total execution time: ~4 seconds, not ~2 seconds
```

This sequential execution is intentional and important for maintaining test isolation. Running tests concurrently could introduce race conditions and side effects where one test could interfere with another, making test results unreliable and difficult to debug.
```