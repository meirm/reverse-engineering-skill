# Just Programmer's Manual - Quick Start

## Overview

The Quick Start section introduces users to the `just` task runner. Begin by installing `just` and verifying the installation with `just --version`.

## Basic Setup

Create a file named `justfile` in your project root:

```
recipe-name:
  echo 'This is a recipe!'

# this is a comment
another-recipe:
  @echo 'This is another recipe.'
```

The system searches for `justfile` case-insensitively (e.g., `Justfile`, `JUSTFILE`) and also recognizes `.justfile` for hidden configurations.

## Running Recipes

**Default behavior** - invoking `just` without arguments executes the first recipe:

```
$ just
echo 'This is a recipe!'
This is a recipe!
```

**Specific recipes** - pass recipe names as arguments:

```
$ just another-recipe
This is another recipe.
```

Commands prefixed with `@` suppress output before execution.

## Recipe Dependencies

Recipes can depend on others, ensuring prerequisites run first:

```
build:
  cc main.c foo.c bar.c -o main

test: build
  ./test
```

Multiple recipes execute in command-line order unless dependencies override this:

```
$ just build sloc
$ just test build  # build runs first despite appearing second
```

## Advanced Features

Recipes can reference submodule recipes:

```
mod foo

baz: foo::bar
```

Command failures halt recipe execution, preventing subsequent steps from running.
