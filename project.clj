
(defproject core-logic-talk "0.1.0-SNAPSHOT"
  :description "Magic Programming Talk Tool"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.logic "0.8.3"]
                 [compojure "1.1.5"]
                 [clojail "1.0.6"]
                 [ring/ring-devel "1.1.8"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [enlive "1.1.1"]
                 [cljs-ajax "0.1.3"]
                 [enfocus "1.0.1"]
                 [rodnaph/lowline "0.0.2"]]
  :plugins [[lein-cljsbuild "0.3.2"]]
  :cljsbuild {
    :builds [{:source-paths ["src/cljs"]
              :compiler {
                :output-to "resources/public/js/application.js"
                :optimizations :whitespace
                :pretty-print true}}]
  }
  :source-paths ["src/clojure" "src/html"]
  :main core-logic-talk.core
  :hooks [leiningen.cljsbuild])

