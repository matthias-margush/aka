# aka

`aka` is for sharing [clojure aliases](https://clojure.org/reference/deps_and_cli#_aliases).

## Installation
Download and put the `aka` script on your path:
```sh
curl -Ss -H 'Cache-Control: no-cache' https://raw.githubusercontent.com/matthias-margush/aka/master/aka >~/bin/aka
chmod +x ~/bin/aka
```

## Usage
```
჻ aka
Usage: aka [command]

Available commands:
tap - Obtain alias definitions.
read - Read and print the definition of an alias.
describe - Print alias documentation.
```

## Tapping
Tapping an alias collection makes it available locally. An alias collection
would typically be provided by a library you're using.

```sh
aka tap :pack https://raw.githubusercontent.com/matthias-margush/aka/master/examples/pack.edn
```

### Example
```sh
჻ aka tap :pack https://raw.githubusercontent.com/matthias-margush/aka/master/examples/pack.edn
Tapped :pack/aws-lambda
Tapped :pack/uberjar
Tapped :pack/onejar
Tapped :pack/skinny

჻ aka :pack/uberjar target/hello_world.jar

჻ ls target
hello_world.jar
```

### Adding aliases into deps.edn
Aliases can be added to a `deps.edn`. Then the alias can be used with `clj
-A:alias`. (Note that as of now, this will rewrite the `deps.edn`, discarding
comments.)

```sh
჻ aka tap -o ~/.clojure/deps.edn :aka https://raw.githubusercontent.com/matthias-margush/aka/master/resources/aka.edn
Tapped :aka/tap
Tapped :aka/read
Tapped :aka/describe

჻ clj -A:aka/describe :aka/describe
Print alias documentation.

Usage:
  aka describe [alias]
  clj -A:aka/describe [alias]

Examples:
  clj -A:aka/describe             # Lists all available aliases
  clj -A:aka/describe :prefix/    # Lists aliases with the namespace of :prefix
  clj -A:aka/describe :prefix/foo # Full description of alias :prefix/foo
```
