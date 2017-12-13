# boot-eval-cljs

Boot task to compile a ClojureScript function and evaluate in Node.js

[![Clojars Project](https://img.shields.io/clojars/v/adamrenklint/boot-eval-cljs.svg?style=flat-square
)](https://clojars.org/adamrenklint/boot-eval-cljs) [![CircleCI](https://img.shields.io/circleci/project/github/adamrenklint/boot-eval-cljs.svg?style=flat-square
)](https://circleci.com/gh/adamrenklint/boot-eval-cljs)

```clojure
[adamrenklint/boot-eval-cljs "1.1.0"] ;; latest release
```

## Usage

Add `boot-eval-cljs` to your `build.boot` dependencies and require the `eval-cljs` task:

```clojure
(set-env! :dependencies '[[adamrenklint/boot-eval-cljs "1.0.0"]])
(require '[adamrenklint.boot-eval-cljs :refer [eval-cljs]])
```

Now you can use the task from the command line. Assuming `print-hello-world` is a function in the `foo.bar` namespace, just run the task with the `-f` option:

```
> boot eval-cljs -f foo.bar/print-hello-world
```

Or use the `eval-cljs` task as a building block when composing other tasks:

```clojure
(deftask dev []
  (comp (watch)
        (eval-cljs :fn 'foo.bar/print-hello-world)))
```

## Options

```
-f, --fn  FUNCTION  Symbol for function to evaluate
```

## License

Copyright (c) 2017 [Adam Renklint](http://adamrenklint.com)

Distributed under the [MIT license](https://github.com/adamrenklint/boot-eval-cljs/blob/master/LICENSE)
