clojure-operator
====
This project is created with [dacom](https://github.com/bellkev/dacom) and modified in directory structure and libs' version
A web application built with the DACOM stack.

## Usage
### Run datomic
`sudo datomic-transactor  ~/Development/clojure-operator/config/free-transactor.properties`
`lein install-db`

### Install JS libraries with `bower`
`bower install`

### Run server with compojure
`lein run-server`

### Run client with om
- lessc is required
`npm install -g less`
`lein run-client`

### Debug, Hack, and Play!

- Run lein cljsbuild auto dev to monitor .cljs files for changes and automatically recompile them

- Run lein watch-less to monitor less for changes and recompile

- Run lein repl to start the usual Leiningen/nREPL-based REPL
Then, if you want to, type (browser-repl) or just (br) into the REPL to start an Austin-based browser REPL session. Once you've done that, refresh the webapp and try typing (js/alert "Hey there!") from the Clojure REPL

## Production-Style Build

- Just run lein dist in the project directory
- Then "deploy" like:

`$ cd dist/server`
`$ echo "{:datomic-uri \"<some-datomic-uri>\" :port 3000}" > config.edn`
`$ java -jar <project-name>-<version-number>-standalone.jar`
`$ Started server on port 3000`

- Then, for example:

`$ cd <project-root>/dist/static && python -m SimpleHTTPServer`
`$ Serving HTTP on 0.0.0.0 port 8000 ...`

- The app will be available on http://localhost:8000

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
