(set-env!
  :resource-paths #{"resources"}
  :target-path "target"
  :source-paths #{"src/clj" "src/cljc"}
  :dependencies '[[org.clojure/clojure "1.9.0-RC1"]
                  [org.clojure/clojurescript "1.9.946"]
                  [org.clojure/core.match "0.3.0-alpha5"]
                  [org.clojure/core.async "0.3.443"
                   :exclusions [org.clojure/tools.reader]]

                  ;; Web
                  [com.stuartsierra/component "0.3.2"]
                  [compojure "1.6.0"]
                  [http-kit "2.2.0"]
                  [ring/ring-defaults "0.3.1"]
                  [metosin/muuntaja "0.3.2"]
                  [com.datomic/datomic-pro "0.9.5561.62"
                   :exclusions [com.google.guava/guava]]
                  [io.rkn/conformity "0.5.1"] ;; Datomic migrations
                  [com.taoensso/sente "1.11.0"
                   :exclusions [com.taoensso/encore]] ;; Sockets and realtime comms
                  [com.taoensso/timbre "4.10.0"]

                  ;; CLJS
                  [reagent "0.7.0"]
                  [re-frame "0.10.2"]
                  [bidi "2.1.2"]])

(deftask dev
  "Profile setup for development mode"
  []
  (System/setProperty "CLJ_ENV" "development")
  (merge-env!
    :resource-paths #{"target"}
    :source-paths #{"env/dev/clj" "test/cljc"}
    :dependencies '[[devcards "0.2.4" :exclusions [cljsjs/react]]
                    [binaryage/devtools "0.9.7"]
                    [reloaded.repl "0.2.4"]
                    [figwheel-sidecar "0.5.14"]
                    [org.clojure/test.check "0.9.0"] ;; For cljs.spec
                    [com.cemerick/piggieback "0.2.2"]
                    [ring/ring-devel "1.6.3"]])
  (task-options! repl {:init-ns 'user
                       :eval `(apply clojure.tools.namespace.repl/set-refresh-dirs
                                ~(get-env :directories))})
  identity)

(deftask prod
  []
  (set-env!))
