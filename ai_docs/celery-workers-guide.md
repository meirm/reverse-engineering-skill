### Navigation

- [index](https://docs.celeryq.dev/en/stable/genindex.html "General Index")
- [modules](https://docs.celeryq.dev/en/stable/py-modindex.html "Python Module Index") |
- [next](https://docs.celeryq.dev/en/stable/userguide/daemonizing.html "Daemonization") |
- [previous](https://docs.celeryq.dev/en/stable/userguide/canvas.html "Canvas: Designing Work-flows") |
- [Celery 5.6.2 documentation](https://docs.celeryq.dev/en/stable/index.html) »
- [User Guide](https://docs.celeryq.dev/en/stable/userguide/index.html) »
- [Workers Guide](https://docs.celeryq.dev/en/stable/userguide/workers.html)

This document describes the current stable version of Celery (5.6).
For development docs,
[go here](https://docs.celeryq.dev/en/main/userguide/workers.html).


# Workers Guide [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#workers-guide "Link to this heading")

## [Starting the worker](https://docs.celeryq.dev/en/stable/userguide/workers.html#id2) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#starting-the-worker "Link to this heading")

You probably want to use a daemonization tool to start
the worker in the background. See [Daemonization](https://docs.celeryq.dev/en/stable/userguide/daemonizing.html) for help
starting the worker as a daemon using popular service managers.

You can start the worker in the foreground by executing the command:

```
$ celery -A proj worker -l INFO
```

For a full list of available command-line options see
[`worker`](https://docs.celeryq.dev/en/stable/reference/celery.bin.worker.html#module-celery.bin.worker "celery.bin.worker"), or simply do:

```
$ celery worker --help
```

You can start multiple workers on the same machine, but
be sure to name each individual worker by specifying a
node name with the [`--hostname`](https://docs.celeryq.dev/en/stable/reference/cli.html#cmdoption-celery-worker-n) argument:

```
$ celery -A proj worker --loglevel=INFO --concurrency=10 -n worker1@%h
$ celery -A proj worker --loglevel=INFO --concurrency=10 -n worker2@%h
$ celery -A proj worker --loglevel=INFO --concurrency=10 -n worker3@%h
```

The `hostname` argument can expand the following variables:

> - `%h`: Hostname, including domain name.
> - `%n`: Hostname only.
> - `%d`: Domain name only.

If the current hostname is _george.example.com_, these will expand to:

| | | |
| --- | --- | --- |
| Variable | Template | Result |
| `%h` | `worker1@%h` | _worker1@george.example.com_ |
| `%n` | `worker1@%n` | _worker1@george_ |
| `%d` | `worker1@%d` | _worker1@example.com_ |

Note for [https://pypi.org/project/supervisor/](https://pypi.org/project/supervisor/) users

The `%` sign must be escaped by adding a second one: %%h.

## [Stopping the worker](https://docs.celeryq.dev/en/stable/userguide/workers.html#stopping-the-worker) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#stopping-the-worker "Link to this heading")

Shutdown should be accomplished using the `TERM` signal.

When shutdown is initiated the worker will finish all currently executing
tasks before it actually terminates. If these tasks are important, you should
wait for it to finish before doing anything drastic, like sending the `KILL`
signal. If the worker won't shutdown after considerate time, for being
stuck in an infinite-loop or similar, you can use the `KILL` signal to
force terminate the worker: but be aware that currently executing tasks will
be lost (i.e., unless the tasks have the `acks_late`
option set).

Also as processes can't override the `KILL` signal, the worker will
not be able to reap its children; make sure to do so manually. This
command usually does the trick:

```
$ pkill -9 -f 'celery worker'
```

If you don't have the __pkill__ command on your system, you can use the slightly
longer version:

```
$ ps auxww | awk '/celery worker/ {print $2}' | xargs kill -9
```

Changed in version 5.2: On Linux systems, Celery now supports sending `KILL` signal to all child processes
after worker termination. This is done via PR_SET_PDEATHSIG option of `prctl(2)`.

### [Worker Shutdown](https://docs.celeryq.dev/en/stable/userguide/workers.html#worker-shutdown) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#worker-shutdown "Link to this heading")

We will use the terms _Warm, Soft, Cold, Hard_ to describe the different stages of worker shutdown.
The worker will initiate the shutdown process when it receives the `TERM` or `QUIT` signal.
The `INT` (Ctrl-C) signal is also handled during the shutdown process and always triggers the
next stage of the shutdown process.

#### [Warm Shutdown](https://docs.celeryq.dev/en/stable/userguide/workers.html#warm-shutdown) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#warm-shutdown "Link to this heading")

When the worker receives the `TERM` signal, it will initiate a warm shutdown. The worker will
finish all currently executing tasks before it actually terminates. The first time the worker receives
the `INT` (Ctrl-C) signal, it will initiate a warm shutdown as well.

The warm shutdown will stop the call to `WorkController.start()`
and will call `WorkController.stop()`.

- Additional `TERM` signals will be ignored during the warm shutdown process.
- The next `INT` signal will trigger the next stage of the shutdown process.

Changed in version 5.6: In previous versions of Celery, when the prefork pool was in use, heartbeats to the broker were not sent during
warm shutdown. This caused the broker to terminate the connection, which meant that tasks were not able to complete.
As of version 5.6, when the prefork pool is in use, heartbeats are now maintained during warm shutdown and tasks are
able to complete before the worker terminates.

#### [Cold Shutdown](https://docs.celeryq.dev/en/stable/userguide/workers.html#cold-shutdown) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#cold-shutdown "Link to this heading")

Cold shutdown is initiated when the worker receives the `QUIT` signal. The worker will stop
all currently executing tasks and terminate immediately.

Note

If the environment variable `REMAP_SIGTERM` is set to `SIGQUIT`, the worker will also initiate
a cold shutdown when it receives the `TERM` signal instead of a warm shutdown.

The cold shutdown will stop the call to `WorkController.start()`
and will call `WorkController.terminate()`.

If the warm shutdown already started, the transition to cold shutdown will run a signal handler `on_cold_shutdown`
to cancel all currently executing tasks from the MainProcess and potentially trigger the Soft Shutdown.

#### [Soft Shutdown](https://docs.celeryq.dev/en/stable/userguide/workers.html#soft-shutdown) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#soft-shutdown "Link to this heading")

Added in version 5.5.

Soft shutdown is a time limited warm shutdown, initiated just before the cold shutdown. The worker will
allow `worker_soft_shutdown_timeout` seconds for all currently executing tasks to finish before
it terminates. If the time limit is reached, the worker will initiate a cold shutdown and cancel all
currently executing tasks. If the `QUIT` signal is received during the soft shutdown, the worker will cancel all
currently executing tasks but still wait for the time limit to finish before terminating, giving a chance for
the worker to perform the cold shutdown a little more gracefully.

The soft shutdown is disabled by default to maintain backward compatibility with the Cold Shutdown
behavior. To enable the soft shutdown, set `worker_soft_shutdown_timeout` to a positive float value.
The soft shutdown will be skipped if there are no tasks running. To force the soft shutdown, _also_ enable the
`worker_enable_soft_shutdown_on_idle` setting.

Warning

If the worker is not running any task but has ETA tasks reserved, the soft shutdown will not be initiated
unless the `worker_enable_soft_shutdown_on_idle` setting is enabled, which may lead to task loss
during the cold shutdown. When using ETA tasks, it is recommended to enable the soft shutdown on idle.
Experiment which `worker_soft_shutdown_timeout` value works best for your setup to reduce the risk
of task loss to a minimum.

For example, when setting `worker_soft_shutdown_timeout=3`, the worker will allow 3 seconds for all currently
executing tasks to finish before it terminates. If the time limit is reached, the worker will initiate a cold shutdown
and cancel all currently executing tasks.

```
[INFO/MainProcess] Task myapp.long_running_task[6f748357-b2c7-456a-95de-f05c00504042] received
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 1/2000s
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 2/2000s
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 3/2000s
^C
worker: Hitting Ctrl+C again will initiate cold shutdown, terminating all running tasks!

worker: Warm shutdown (MainProcess)
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 4/2000s
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 5/2000s
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 6/2000s
^C
worker: Hitting Ctrl+C again will terminate all running tasks!
[WARNING/MainProcess] Initiating Soft Shutdown, terminating in 3 seconds
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 7/2000s
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 8/2000s
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 9/2000s
[WARNING/MainProcess] Restoring 1 unacknowledged message(s)
```

- The next `QUIT` signal will cancel the tasks that are still running in the soft shutdown, but the worker
  will still wait for the time limit to finish before terminating.
- The next (2nd) `QUIT` or `INT` signal will trigger the next stage of the shutdown process.

#### [Hard Shutdown](https://docs.celeryq.dev/en/stable/userguide/workers.html#hard-shutdown) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#hard-shutdown "Link to this heading")

Added in version 5.5.

Hard shutdown is mostly for local or debug purposes, allowing to spam the `INT` (Ctrl-C) signal
to force the worker to terminate immediately. The worker will stop all currently executing tasks and
terminate immediately by raising a `WorkerTerminate` exception in the MainProcess.

For example, notice the `^C` in the logs below (using the `INT` signal to move from stage to stage):

```
[INFO/MainProcess] Task myapp.long_running_task[7235ac16-543d-4fd5-a9e1-2d2bb8ab630a] received
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 1/2000s
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 2/2000s
^C
worker: Hitting Ctrl+C again will initiate cold shutdown, terminating all running tasks!

worker: Warm shutdown (MainProcess)
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 3/2000s
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 4/2000s
^C
worker: Hitting Ctrl+C again will terminate all running tasks!
[WARNING/MainProcess] Initiating Soft Shutdown, terminating in 10 seconds
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 5/2000s
[WARNING/ForkPoolWorker-8] long_running_task is running, sleeping 6/2000s
^C
Waiting gracefully for cold shutdown to complete...

worker: Cold shutdown (MainProcess)
^C[WARNING/MainProcess] Restoring 1 unacknowledged message(s)
```

Warning

The log `Restoring 1 unacknowledged message(s)` is misleading as it is not guaranteed that the message
will be restored after a hard shutdown. The Soft Shutdown allows adding a time window just between
the warm and the cold shutdown that improves the gracefulness of the shutdown process.

## [Restarting the worker](https://docs.celeryq.dev/en/stable/userguide/workers.html#restarting-the-worker) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#restarting-the-worker "Link to this heading")

To restart the worker you should send the TERM signal and start a new
instance. The easiest way to manage workers for development
is by using celery multi:

```
$ celery multi start 1 -A proj -l INFO -c4 --pidfile=/var/run/celery/%n.pid
$ celery multi restart 1 --pidfile=/var/run/celery/%n.pid
```

For production deployments you should be using init-scripts or a process
supervision system (see [Daemonization](https://docs.celeryq.dev/en/stable/userguide/daemonizing.html)).

Other than stopping, then starting the worker to restart, you can also
restart the worker using the `HUP` signal. Note that the worker
will be responsible for restarting itself so this is prone to problems and
isn't recommended in production:

```
$ kill -HUP $pid
```

Note

Restarting by `HUP` only works if the worker is running
in the background as a daemon (it doesn't have a controlling
terminal).

`HUP` is disabled on macOS because of a limitation on
that platform.

## [Automatic re-connection on connection loss to broker](https://docs.celeryq.dev/en/stable/userguide/workers.html#automatic-re-connection-on-connection-loss-to-broker) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#automatic-re-connection-on-connection-loss-to-broker "Link to this heading")

Added in version 5.3.

Unless `broker_connection_retry_on_startup` is set to False,
Celery will automatically retry reconnecting to the broker after the first
connection loss. `broker_connection_retry` controls whether to automatically
retry reconnecting to the broker for subsequent reconnects.

Added in version 5.1.

If `worker_cancel_long_running_tasks_on_connection_loss` is set to True,
Celery will also cancel any long running task that is currently running.

Added in version 5.3.

Since the message broker does not track how many tasks were already fetched before
the connection was lost, Celery will reduce the prefetch count by the number of
tasks that are currently running multiplied by `worker_prefetch_multiplier`.
The prefetch count will be gradually restored to the maximum allowed after
each time a task that was running before the connection was lost is complete.

This feature is enabled by default, but can be disabled by setting False
to `worker_enable_prefetch_count_reduction`.

## [Process Signals](https://docs.celeryq.dev/en/stable/userguide/workers.html#process-signals) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#process-signals "Link to this heading")

The worker's main process overrides the following signals:

| | |
| --- | --- |
| `TERM` | Warm shutdown, wait for tasks to complete. |
| `QUIT` | Cold shutdown, terminate ASAP |
| `USR1` | Dump traceback for all active threads. |
| `USR2` | Remote debug, see `celery.contrib.rdb`. |

## [Variables in file paths](https://docs.celeryq.dev/en/stable/userguide/workers.html#variables-in-file-paths) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#variables-in-file-paths "Link to this heading")

The file path arguments for `--logfile`,
`--pidfile`, and
`--statedb` can contain variables that the
worker will expand:

### [Node name replacements](https://docs.celeryq.dev/en/stable/userguide/workers.html#node-name-replacements) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#node-name-replacements "Link to this heading")

- `%p`: Full node name.
- `%h`: Hostname, including domain name.
- `%n`: Hostname only.
- `%d`: Domain name only.
- `%i`: Prefork pool process index or 0 if MainProcess.
- `%I`: Prefork pool process index with separator.

For example, if the current hostname is `george@foo.example.com` then
these will expand to:

- `--logfile=%p.log` -> `george@foo.example.com.log`
- `--logfile=%h.log` -> `foo.example.com.log`
- `--logfile=%n.log` -> `george.log`
- `--logfile=%d.log` -> `example.com.log`

### [Prefork pool process index](https://docs.celeryq.dev/en/stable/userguide/workers.html#prefork-pool-process-index) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#prefork-pool-process-index "Link to this heading")

The prefork pool process index specifiers will expand into a different
filename depending on the process that'll eventually need to open the file.

This can be used to specify one log file per child process.

Note that the numbers will stay within the process limit even if processes
exit or if autoscale/`maxtasksperchild`/time limits are used. That is, the number
is the _process index_ not the process count or pid.

- `%i` - Pool process index or 0 if MainProcess.

  > Where `-n worker1@example.com -c2 -f %n-%i.log` will result in
  > three log files:
  >
  > > - `worker1-0.log` (main process)
  > > - `worker1-1.log` (pool process 1)
  > > - `worker1-2.log` (pool process 2)
- `%I` - Pool process index with separator.

  > Where `-n worker1@example.com -c2 -f %n%I.log` will result in
  > three log files:
  >
  > > - `worker1.log` (main process)
  > > - `worker1-1.log` (pool process 1)
  > > - `worker1-2.log` (pool process 2)

## [Concurrency](https://docs.celeryq.dev/en/stable/userguide/workers.html#concurrency) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#concurrency "Link to this heading")

By default multiprocessing is used to perform concurrent execution of tasks,
but you can also use Eventlet. The number
of worker processes/threads can be changed using the
`--concurrency` argument and defaults
to the number of CPUs available on the machine.

Number of processes (multiprocessing/prefork pool)

More pool processes are usually better, but there's a cut-off point where
adding more pool processes affects performance in negative ways.
There's even some evidence to support that having multiple worker
instances running, may perform better than having a single worker.
For example 3 workers with 10 pool processes each. You need to experiment
to find the numbers that works best for you, as this varies based on
application, work load, task run times and other factors.

## [Remote control](https://docs.celeryq.dev/en/stable/userguide/workers.html#remote-control) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#remote-control "Link to this heading")

Added in version 2.0.

The `celery` command

The __celery__ program is used to execute remote control
commands from the command-line. It supports all of the commands
listed below. See [Management Command-line Utilities (inspect/control)](https://docs.celeryq.dev/en/stable/userguide/monitoring.html#management) for more information.

pool support:
:   _prefork, eventlet, gevent, thread_, blocking:_solo_ (see note)

broker support:
:   _amqp, redis_

Workers have the ability to be remote controlled using a high-priority
broadcast message queue. The commands can be directed to all, or a specific
list of workers.

Commands can also have replies. The client can then wait for and collect
those replies. Since there's no central authority to know how many
workers are available in the cluster, there's also no way to estimate
how many workers may send a reply, so the client has a configurable
timeout — the deadline in seconds for replies to arrive in. This timeout
defaults to one second. If the worker doesn't reply within the deadline
it doesn't necessarily mean the worker didn't reply, or worse is dead, but
may simply be caused by network latency or the worker being slow at processing
commands, so adjust the timeout accordingly.

In addition to timeouts, the client can specify the maximum number
of replies to wait for. If a destination is specified, this limit is set
to the number of destination hosts.

Note

The `solo` pool supports remote control commands,
but any task executing will block any waiting control command,
so it is of limited use if the worker is very busy. In that
case you must increase the timeout waiting for replies in the client.

### [The `broadcast()` function](https://docs.celeryq.dev/en/stable/userguide/workers.html#the-broadcast-function) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#the-broadcast-function "Link to this heading")

This is the client function used to send commands to the workers.
Some remote control commands also have higher-level interfaces using
`broadcast()` in the background, like
`rate_limit()`, and `ping()`.

Sending the `rate_limit` command and keyword arguments:

```
>>> app.control.broadcast('rate_limit',
...                          arguments={'task_name': 'myapp.mytask',
...                                     'rate_limit': '200/m'})
```

This will send the command asynchronously, without waiting for a reply.
To request a reply you have to use the reply argument:

```
>>> app.control.broadcast('rate_limit', {
...     'task_name': 'myapp.mytask', 'rate_limit': '200/m'}, reply=True)
[{'worker1.example.com': 'New rate limit set successfully'},
 {'worker2.example.com': 'New rate limit set successfully'},
 {'worker3.example.com': 'New rate limit set successfully'}]
```

Using the destination argument you can specify a list of workers
to receive the command:

```
>>> app.control.broadcast('rate_limit', {
...     'task_name': 'myapp.mytask',
...     'rate_limit': '200/m'}, reply=True,
...                             destination=['worker1@example.com'])
[{'worker1.example.com': 'New rate limit set successfully'}]
```

Of course, using the higher-level interface to set rate limits is much
more convenient, but there are commands that can only be requested
using `broadcast()`.

## [Commands](https://docs.celeryq.dev/en/stable/userguide/workers.html#commands) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#commands "Link to this heading")

### [`revoke`: Revoking tasks](https://docs.celeryq.dev/en/stable/userguide/workers.html#revoke-revoking-tasks) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#revoke-revoking-tasks "Link to this heading")

pool support:
:   all, terminate only supported by prefork, eventlet and gevent

broker support:
:   _amqp, redis_

command:
:   __celery -A proj control revoke <task_id>__

All worker nodes keeps a memory of revoked task ids, either in-memory or
persistent on disk (see [Persistent revokes](https://docs.celeryq.dev/en/stable/userguide/workers.html#persistent-revokes)).

Note

The maximum number of revoked tasks to keep in memory can be
specified using the `CELERY_WORKER_REVOKES_MAX` environment
variable, which defaults to 50000. When the limit has been exceeded,
the revokes will be active for 10800 seconds (3 hours) before being
expired. This value can be changed using the
`CELERY_WORKER_REVOKE_EXPIRES` environment variable.

Memory limits can also be set for successful tasks through the
`CELERY_WORKER_SUCCESSFUL_MAX` and
`CELERY_WORKER_SUCCESSFUL_EXPIRES` environment variables, and
default to 1000 and 10800 respectively.

When a worker receives a revoke request it will skip executing
the task, but it won't terminate an already executing task unless
the terminate option is set.

Note

The terminate option is a last resort for administrators when
a task is stuck. It's not for terminating the task,
it's for terminating the process that's executing the task, and that
process may have already started processing another task at the point
when the signal is sent, so for this reason you must never call this
programmatically.

If terminate is set the worker child process processing the task
will be terminated. The default signal sent is TERM, but you can
specify this using the signal argument. Signal can be the uppercase name
of any signal defined in the `signal` module in the Python Standard
Library.

Terminating a task also revokes it.

Changed in version 5.6: When a task is revoked, the result backend is now immediately updated
to reflect the `REVOKED` status. Previously, the backend was only
updated when a worker attempted to process the revoked task, which
could leave tasks with ETA/countdown in `PENDING` status indefinitely
if the worker was shut down before the scheduled time.

__Example__

```
>>> result.revoke()

>>> AsyncResult(id).revoke()

>>> app.control.revoke('d9078da5-9915-40a0-bfa1-392c7bde42ed')

>>> app.control.revoke('d9078da5-9915-40a0-bfa1-392c7bde42ed',
...                    terminate=True)

>>> app.control.revoke('d9078da5-9915-40a0-bfa1-392c7bde42ed',
...                    terminate=True, signal='SIGKILL')
```

#### [Revoking multiple tasks](https://docs.celeryq.dev/en/stable/userguide/workers.html#revoking-multiple-tasks) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#revoking-multiple-tasks "Link to this heading")

Added in version 3.1.

The revoke method also accepts a list argument, where it will revoke
several tasks at once.

__Example__

```
>>> app.control.revoke([
...    '7993b0aa-1f0b-4780-9af0-c47c0858b3f2',
...    'f565793e-b041-4b2b-9ca4-dca22762a55d',
...    'd9d35e03-2997-42d0-a13e-64a66b88a618',
])
```

The `GroupResult.revoke` method takes advantage of this since
version 3.1.

#### [Persistent revokes](https://docs.celeryq.dev/en/stable/userguide/workers.html#persistent-revokes) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#persistent-revokes "Link to this heading")

Revoking tasks works by sending a broadcast message to all the workers,
the workers then keep a list of revoked tasks in memory. When a worker starts
up it will synchronize revoked tasks with other workers in the cluster.

The list of revoked tasks is in-memory so if all workers restart the list
of revoked ids will also vanish. If you want to preserve this list between
restarts you need to specify a file for these to be stored in by using the –statedb
argument to __celery worker__:

```
$ celery -A proj worker -l INFO --statedb=/var/run/celery/worker.state
```

or if you use __celery multi__ you want to create one file per
worker instance so use the %n format to expand the current node
name:

```
celery multi start 2 -l INFO --statedb=/var/run/celery/%n.state
```

See also [Variables in file paths](https://docs.celeryq.dev/en/stable/userguide/workers.html#variables-in-file-paths)

Note that remote control commands must be working for revokes to work.
Remote control commands are only supported by the RabbitMQ (amqp) and Redis
at this point.

### [`revoke_by_stamped_headers`: Revoking tasks by their stamped headers](https://docs.celeryq.dev/en/stable/userguide/workers.html#revoke-by-stamped-headers-revoking-tasks-by-their-stamped-headers) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#revoke-by-stamped-headers-revoking-tasks-by-their-stamped-headers "Link to this heading")

pool support:
:   all, terminate only supported by prefork and eventlet

broker support:
:   _amqp, redis_

command:
:   __celery -A proj control revoke_by_stamped_headers <header=value>__

This command is similar to `revoke()`, but instead of
specifying the task id(s), you specify the stamped header(s) as key-value pair(s),
and each task that has a stamped header matching the key-value pair(s) will be revoked.

Warning

The revoked headers mapping is not persistent across restarts, so if you
restart the workers, the revoked headers will be lost and need to be
mapped again.

Warning

This command may perform poorly if your worker pool concurrency is high
and terminate is enabled, since it will have to iterate over all the running
tasks to find the ones with the specified stamped header.

__Example__

```
>>> app.control.revoke_by_stamped_headers({'header': 'value'})

>>> app.control.revoke_by_stamped_headers({'header': 'value'}, terminate=True)

>>> app.control.revoke_by_stamped_headers({'header': 'value'}, terminate=True, signal='SIGKILL')
```

#### [Revoking multiple tasks by stamped headers](https://docs.celeryq.dev/en/stable/userguide/workers.html#revoking-multiple-tasks-by-stamped-headers) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#revoking-multiple-tasks-by-stamped-headers "Link to this heading")

Added in version 5.3.

The `revoke_by_stamped_headers` method also accepts a list argument, where it will revoke
by several headers or several values.

__Example__

```
>> app.control.revoke_by_stamped_headers({
...    'header_A': 'value_1',
...    'header_B': ['value_2', 'value_3'],
})
```

This will revoke all of the tasks that have a stamped header `header_A` with value `value_1`,
and all of the tasks that have a stamped header `header_B` with values `value_2` or `value_3`.

__CLI Example__

```
$ celery -A proj control revoke_by_stamped_headers stamped_header_key_A=stamped_header_value_1 stamped_header_key_B=stamped_header_value_2

$ celery -A proj control revoke_by_stamped_headers stamped_header_key_A=stamped_header_value_1 stamped_header_key_B=stamped_header_value_2 --terminate

$ celery -A proj control revoke_by_stamped_headers stamped_header_key_A=stamped_header_value_1 stamped_header_key_B=stamped_header_value_2 --terminate --signal=SIGKILL
```

## [Time Limits](https://docs.celeryq.dev/en/stable/userguide/workers.html#time-limits) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#time-limits "Link to this heading")

Added in version 2.0.

pool support:
:   _prefork/gevent (see note below)_

Soft, or hard?

The time limit is set in two values, soft and hard.
The soft time limit allows the task to catch an exception
to clean up before it is killed: the hard timeout isn't catch-able
and force terminates the task.

A single task can potentially run forever, if you have lots of tasks
waiting for some event that'll never happen you'll block the worker
from processing new tasks indefinitely. The best way to defend against
this scenario happening is enabling time limits.

The time limit (–time-limit) is the maximum number of seconds a task
may run before the process executing it is terminated and replaced by a
new process. You can also enable a soft time limit (–soft-time-limit),
this raises an exception the task can catch to clean up before the hard
time limit kills it:

```
from myapp import app
from celery.exceptions import SoftTimeLimitExceeded

@app.task
def mytask():
    try:
        do_work()
    except SoftTimeLimitExceeded:
        clean_up_in_a_hurry()
```

Time limits can also be set using the `task_time_limit` /
`task_soft_time_limit` settings. You can also specify time
limits for client side operation using `timeout` argument of
`AsyncResult.get()` function.

Note

Time limits don't currently work on platforms that don't support
the `SIGUSR1` signal.

Note

The gevent pool does not implement soft time limits. Additionally,
it will not enforce the hard time limit if the task is blocking.

#### [Changing time limits at run-time](https://docs.celeryq.dev/en/stable/userguide/workers.html#changing-time-limits-at-run-time) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#changing-time-limits-at-run-time "Link to this heading")

Added in version 2.3.

broker support:
:   _amqp, redis_

There's a remote control command that enables you to change both soft
and hard time limits for a task — named `time_limit`.

Example changing the time limit for the `tasks.crawl_the_web` task
to have a soft time limit of one minute, and a hard time limit of
two minutes:

```
>>> app.control.time_limit('tasks.crawl_the_web',
                           soft=60, hard=120, reply=True)
[{'worker1.example.com': {'ok': 'time limits set successfully'}}]
```

Only tasks that starts executing after the time limit change will be affected.

## [Rate Limits](https://docs.celeryq.dev/en/stable/userguide/workers.html#rate-limits) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#rate-limits "Link to this heading")

#### [Changing rate-limits at run-time](https://docs.celeryq.dev/en/stable/userguide/workers.html#changing-rate-limits-at-run-time) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#changing-rate-limits-at-run-time "Link to this heading")

Example changing the rate limit for the myapp.mytask task to execute
at most 200 tasks of that type every minute:

```
>>> app.control.rate_limit('myapp.mytask', '200/m')
```

The above doesn't specify a destination, so the change request will affect
all worker instances in the cluster. If you only want to affect a specific
list of workers you can include the `destination` argument:

```
>>> app.control.rate_limit('myapp.mytask', '200/m',
...            destination=['celery@worker1.example.com'])
```

Warning

This won't affect workers with the
`worker_disable_rate_limits` setting enabled.

## [Max tasks per child setting](https://docs.celeryq.dev/en/stable/userguide/workers.html#max-tasks-per-child-setting) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#max-tasks-per-child-setting "Link to this heading")

Added in version 2.0.

pool support:
:   _prefork_

With this option you can configure the maximum number of tasks
a worker can execute before it's replaced by a new process.

This is useful if you have memory leaks you have no control over
for example from closed source C extensions.

The option can be set using the workers
`--max-tasks-per-child` argument
or using the `worker_max_tasks_per_child` setting.

## [Max memory per child setting](https://docs.celeryq.dev/en/stable/userguide/workers.html#max-memory-per-child-setting) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#max-memory-per-child-setting "Link to this heading")

Added in version 4.0.

pool support:
:   _prefork_

With this option you can configure the maximum amount of resident
memory a worker can execute before it's replaced by a new process.

This is useful if you have memory leaks you have no control over
for example from closed source C extensions.

The option can be set using the workers
`--max-memory-per-child` argument
or using the `worker_max_memory_per_child` setting.

## [Autoscaling](https://docs.celeryq.dev/en/stable/userguide/workers.html#autoscaling) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#autoscaling "Link to this heading")

Added in version 2.2.

pool support:
:   _prefork_, _gevent_

The _autoscaler_ component is used to dynamically resize the pool
based on load:

- The autoscaler adds more pool processes when there is work to do,
  :   - and starts removing processes when the workload is low.

It's enabled by the `--autoscale` option,
which needs two numbers: the maximum and minimum number of pool processes:

```
--autoscale=AUTOSCALE
     Enable autoscaling by providing
     max_concurrency,min_concurrency.  Example:
       --autoscale=10,3 (always keep 3 processes, but grow to
      10 if necessary).
```

You can also define your own rules for the autoscaler by subclassing
`Autoscaler`.
Some ideas for metrics include load average or the amount of memory available.
You can specify a custom autoscaler with the `worker_autoscaler` setting.

## [Queues](https://docs.celeryq.dev/en/stable/userguide/workers.html#queues) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#queues "Link to this heading")

A worker instance can consume from any number of queues.
By default it will consume from all queues defined in the
`task_queues` setting (that if not specified falls back to the
default queue named `celery`).

You can specify what queues to consume from at start-up, by giving a comma
separated list of queues to the `-Q` option:

```
$ celery -A proj worker -l INFO -Q foo,bar,baz
```

If the queue name is defined in `task_queues` it will use that
configuration, but if it's not defined in the list of queues Celery will
automatically generate a new queue for you (depending on the
`task_create_missing_queues` option).

You can also tell the worker to start and stop consuming from a queue at
run-time using the remote control commands `add_consumer` and
`cancel_consumer`.

### [Queues: Adding consumers](https://docs.celeryq.dev/en/stable/userguide/workers.html#queues-adding-consumers) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#queues-adding-consumers "Link to this heading")

The `add_consumer` control command will tell one or more workers
to start consuming from a queue. This operation is idempotent.

To tell all workers in the cluster to start consuming from a queue
named "`foo`" you can use the __celery control__ program:

```
$ celery -A proj control add_consumer foo
-> worker1.local: OK
    started consuming from u'foo'
```

If you want to specify a specific worker you can use the
`--destination` argument:

```
$ celery -A proj control add_consumer foo -d celery@worker1.local
```

The same can be accomplished dynamically using the `app.control.add_consumer()` method:

```
>>> app.control.add_consumer('foo', reply=True)
[{u'worker1.local': {u'ok': u"already consuming from u'foo'"}}]

>>> app.control.add_consumer('foo', reply=True,
...                          destination=['worker1@example.com'])
[{u'worker1.local': {u'ok': u"already consuming from u'foo'"}}]
```

By now we've only shown examples using automatic queues,
If you need more control you can also specify the exchange, routing_key and
even other options:

```
>>> app.control.add_consumer(
...     queue='baz',
...     exchange='ex',
...     exchange_type='topic',
...     routing_key='media.*',
...     options={
...         'queue_durable': False,
...         'exchange_durable': False,
...     },
...     reply=True,
...     destination=['w1@example.com', 'w2@example.com'])
```

### [Queues: Canceling consumers](https://docs.celeryq.dev/en/stable/userguide/workers.html#queues-canceling-consumers) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#queues-canceling-consumers "Link to this heading")

You can cancel a consumer by queue name using the `cancel_consumer`
control command.

To force all workers in the cluster to cancel consuming from a queue
you can use the __celery control__ program:

```
$ celery -A proj control cancel_consumer foo
```

The `--destination` argument can be
used to specify a worker, or a list of workers, to act on the command:

```
$ celery -A proj control cancel_consumer foo -d celery@worker1.local
```

You can also cancel consumers programmatically using the
`app.control.cancel_consumer()` method:

```
>>> app.control.cancel_consumer('foo', reply=True)
[{u'worker1.local': {u'ok': u"no longer consuming from u'foo'"}}]
```

### [Queues: List of active queues](https://docs.celeryq.dev/en/stable/userguide/workers.html#queues-list-of-active-queues) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#queues-list-of-active-queues "Link to this heading")

You can get a list of queues that a worker consumes from by using
the `active_queues` control command:

```
$ celery -A proj inspect active_queues
[...]
```

Like all other remote control commands this also supports the
`--destination` argument used
to specify the workers that should reply to the request:

```
$ celery -A proj inspect active_queues -d celery@worker1.local
[...]
```

This can also be done programmatically by using the
`active_queues()` method:

```
>>> app.control.inspect().active_queues()
[...]

>>> app.control.inspect(['worker1.local']).active_queues()
[...]
```

## [Inspecting workers](https://docs.celeryq.dev/en/stable/userguide/workers.html#inspecting-workers) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#inspecting-workers "Link to this heading")

`app.control.inspect` lets you inspect running workers. It
uses remote control commands under the hood.

You can also use the `celery` command to inspect workers,
and it supports the same commands as the `app.control` interface.

```
>>> # Inspect all nodes.
>>> i = app.control.inspect()

>>> # Specify multiple nodes to inspect.
>>> i = app.control.inspect(['worker1.example.com',
                            'worker2.example.com'])

>>> # Specify a single node to inspect.
>>> i = app.control.inspect('worker1.example.com')
```

### [Dump of registered tasks](https://docs.celeryq.dev/en/stable/userguide/workers.html#dump-of-registered-tasks) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#dump-of-registered-tasks "Link to this heading")

You can get a list of tasks registered in the worker using the
`registered()`:

```
>>> i.registered()
[{'worker1.example.com': ['tasks.add',
                          'tasks.sleeptask']}]
```

### [Dump of currently executing tasks](https://docs.celeryq.dev/en/stable/userguide/workers.html#dump-of-currently-executing-tasks) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#dump-of-currently-executing-tasks "Link to this heading")

You can get a list of active tasks using
`active()`:

```
>>> i.active()
[{'worker1.example.com':
    [{'name': 'tasks.sleeptask',
      'id': '32666e9b-809c-41fa-8e93-5ae0c80afbbf',
      'args': '(8,)',
      'kwargs': '{}'}]}]
```

### [Dump of scheduled (ETA) tasks](https://docs.celeryq.dev/en/stable/userguide/workers.html#dump-of-scheduled-eta-tasks) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#dump-of-scheduled-eta-tasks "Link to this heading")

You can get a list of tasks waiting to be scheduled by using
`scheduled()`:

```
>>> i.scheduled()
[{'worker1.example.com':
    [{'eta': '2010-06-07 09:07:52', 'priority': 0,
      'request': {
        'name': 'tasks.sleeptask',
        'id': '1a7980ea-8b19-413e-91d2-0b74f3844c4d',
        'args': '[1]',
        'kwargs': '{}'}},
     {'eta': '2010-06-07 09:07:53', 'priority': 0,
      'request': {
        'name': 'tasks.sleeptask',
        'id': '49661b9a-aa22-4120-94b7-9ee8031d219d',
        'args': '[2]',
        'kwargs': '{}'}}]}]
```

Note

These are tasks with an ETA/countdown argument, not periodic tasks.

### [Dump of reserved tasks](https://docs.celeryq.dev/en/stable/userguide/workers.html#dump-of-reserved-tasks) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#dump-of-reserved-tasks "Link to this heading")

Reserved tasks are tasks that have been received, but are still waiting to be
executed.

You can get a list of these using
`reserved()`:

```
>>> i.reserved()
[{'worker1.example.com':
    [{'name': 'tasks.sleeptask',
      'id': '32666e9b-809c-41fa-8e93-5ae0c80afbbf',
      'args': '(8,)',
      'kwargs': '{}'}]}]
```

### [Statistics](https://docs.celeryq.dev/en/stable/userguide/workers.html#statistics) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#statistics "Link to this heading")

The remote control command `inspect stats` (or
`stats()`) will give you a long list of useful (or not
so useful) statistics about the worker:

```
$ celery -A proj inspect stats
```

For the output details, consult the reference documentation of `stats()`.

## [Additional Commands](https://docs.celeryq.dev/en/stable/userguide/workers.html#additional-commands) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#additional-commands "Link to this heading")

### [Remote shutdown](https://docs.celeryq.dev/en/stable/userguide/workers.html#remote-shutdown) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#remote-shutdown "Link to this heading")

This command will gracefully shut down the worker remotely:

```
>>> app.control.broadcast('shutdown') # shutdown all workers
>>> app.control.broadcast('shutdown', destination='worker1@example.com')
```

### [Ping](https://docs.celeryq.dev/en/stable/userguide/workers.html#ping) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#ping "Link to this heading")

This command requests a ping from alive workers.
The workers reply with the string 'pong', and that's just about it.
It will use the default one second timeout for replies unless you specify
a custom timeout:

```
>>> app.control.ping(timeout=0.5)
[{'worker1.example.com': 'pong'},
 {'worker2.example.com': 'pong'},
 {'worker3.example.com': 'pong'}]
```

`ping()` also supports the destination argument,
so you can specify the workers to ping:

```
>>> ping(['worker2.example.com', 'worker3.example.com'])
[{'worker2.example.com': 'pong'},
 {'worker3.example.com': 'pong'}]
```

### [Enable/disable events](https://docs.celeryq.dev/en/stable/userguide/workers.html#enable-disable-events) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#enable-disable-events "Link to this heading")

You can enable/disable events by using the enable_events,
disable_events commands. This is useful to temporarily monitor
a worker using __celery events__/__celerymon__.

```
>>> app.control.enable_events()
>>> app.control.disable_events()
```

## [Writing your own remote control commands](https://docs.celeryq.dev/en/stable/userguide/workers.html#writing-your-own-remote-control-commands) [¶](https://docs.celeryq.dev/en/stable/userguide/workers.html#writing-your-own-remote-control-commands "Link to this heading")

There are two types of remote control commands:

- Inspect command

  > Does not have side effects, will usually just return some value
  > found in the worker, like the list of currently registered tasks,
  > the list of active tasks, etc.

- Control command

  > Performs side effects, like adding a new queue to consume from.

Remote control commands are registered in the control panel and
they take a single argument: the current
`celery.worker.control.ControlDispatch` instance.
From there you have access to the active
`Consumer` if needed.

Here's an example control command that increments the task prefetch count:

```
from celery.worker.control import control_command

@control_command(
    args=[('n', int)],
    signature='[N=1]',  # <- used for help on the command-line.
)
def increase_prefetch_count(state, n=1):
    state.consumer.qos.increment_eventually(n)
    return {'ok': 'prefetch count incremented'}
```

Make sure you add this code to a module that is imported by the worker:
this could be the same module as where your Celery app is defined, or you
can add the module to the `imports` setting.

Restart the worker so that the control command is registered, and now you
can call your command using the __celery control__ utility:

```
$ celery -A proj control increase_prefetch_count 3
```

You can also add actions to the __celery inspect__ program,
for example one that reads the current prefetch count:

```
from celery.worker.control import inspect_command

@inspect_command()
def current_prefetch_count(state):
    return {'prefetch_count': state.consumer.qos.value}
```

After restarting the worker you can now query this value using the
__celery inspect__ program:

```
$ celery -A proj inspect current_prefetch_count
```