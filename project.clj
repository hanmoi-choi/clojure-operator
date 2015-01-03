;   Copyright (c) 2014 Kevin Bell. All rights reserved.
;   See the file license.txt for copying permission.

(defproject clojure-operator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 ;; WEB
                 [ring/ring-core "1.3.2"]
                 [ring/ring-json "0.3.1"]
                 [ring-cors "0.1.6"]
                 [ring-middleware-format "0.4.0"]
                 [compojure "1.3.1"]
                 ;; Async
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 ;; Clojure Script
                 [org.clojure/clojurescript "0.0-2511"]
                 [cljs-ajax "0.3.3"]
                 [cljs-http "0.1.23"]

                 ;;HTML Templating
                 [selmer "0.7.8"]
                 [sablono "0.2.2"]
                 ;;React
                 [om "0.8.0-beta5"]
                 ;;Cljs REPL
                 [com.cemerick/austin "0.1.5"]
                 ;; DB
                 [com.datomic/datomic-free "0.9.5067" :exclusions [joda-time]]
                 ;; RabbitMQ
                 [com.novemberain/langohr "3.0.1"]
                 ;; Component
                 [com.stuartsierra/component "0.2.2"]]


  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-ring "0.8.13"]
            [lein-pdo "0.1.1"]
            [lein-figwheel "0.2.0-SNAPSHOT"]
            [lein-resource "0.3.1"]
            [lein-httpd "1.0.0"]
            [lein-shell "0.3.0"]
            [fsrun "0.1.2"]]


  :source-paths ["src"]
  :target-path "target/"
  :uberjar-exclusions [#".*\.cljs"]
  :cljsbuild {:builds {:dev {:source-paths ["utils/src" "src/com/myadbox/cljs"]
                             :compiler {:output-to "static/js/main.js"
                                        :output-dir "static/js"
                                        :optimizations :none
                                        :pretty-print true
                                        :source-map true}}
                       :prod {:source-paths ["src"]
                              :compiler {:output-to "dist/static/js/main.js"
                                         :optimizations :advanced
                                         :pretty-print false
                                         ;; From Om jar
                                         :preamble ["react/react.min.js"]
                                         :externs ["react/externs/react.js"]}}}}
  :ring {:init com.myadbox.clj.server/init-conn
         :handler com.myadbox.clj.server/app}
  :profiles {:dev {
                    ;; This needs to be here because of https://github.com/cemerick/austin/issues/23
                    :plugins [[com.cemerick/austin "0.1.5"]]
                    :source-paths ["utils/src"]
                    :repl-options {:init-ns clojure-operator.repl}
                    :resource {:resource-paths ["web-resources/pages"]
                               :target-path "static"
                               :extra-values {:scripts [{:src "../bower_components/react/react.js"}
                                                        {:src "js/goog/base.js"}
                                                        {:src "js/main.js"}
                                                        {:body "goog.require('com.myadbox.cljs.client')"}
                                                        {:body "goog.require('clojure_operator.repl')"}]}}}
             :db [:dev {:main clojure-operator.db}]
             :prod {:main clojure-operator.server
                    :target-path "dist/server/"
                    :resource {:resource-paths ["web-resources/pages"]
                               :target-path "dist/static"
                               :extra-values {:scripts [{:src "js/main.js"}]}}}
             :uberjar {:omit-source true
                       :aot :all}}
  :aliases {"bower" ["shell" "bower" "install"]
            "less-debug" ["shell" "lessc" "web-resources/stylesheets/style.less" "static/css/style.css"
                          "--include-path=bower_components/bootstrap/less/" "--source-map"]
            "less-prod" ["shell" "lessc" "web-resources/stylesheets/style.less" "dist/static/css/style.css"
                         "--include-path=bower_components/bootstrap/less/" "--compress"]
            "watch-less" ["fschange" "web-resources/stylesheets/*" "less-debug"]
            "install-db" ["with-profile" "db" "run"]
            "run-client" ["do" "bower," "cljsbuild" "once" "dev," "less-debug," "resource," "httpd" "8000"]
            "run-server" ["ring" "server-headless"]
            "dist" ["with-profile" "prod" "do" "bower," "uberjar," "cljsbuild" "once" "prod," "less-prod,"
                    "resource"]}
  :clean-targets [:target-path :compile-path "static" "dist"])
