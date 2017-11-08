(defproject sheet-bucket "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.6.1"

  :dependencies [[org.clojure/clojure "1.9.0-RC1"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/core.match "0.3.0-alpha5"]
                 [org.clojure/core.async "0.3.443"
                  :exclusions [org.clojure/tools.reader]]

                 ;; Web
                 [com.stuartsierra/component "0.3.2"]
                 [compojure "1.6.0"]
                 [http-kit "2.2.0"]
                 [ring/ring-devel "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-json "0.4.0"]
                 [com.datomic/clj-client "0.8.606"]

                 ;; CLJS
                 [reagent "0.7.0"]
                 [re-frame "0.10.2"]
                 [cljs-ajax "0.7.3"]]

  :profiles {:dev {:dependencies [[devcards "0.2.4" :exclusions [cljsjs/react]]
                                  [binaryage/devtools "0.9.7"]
                                  [reloaded.repl "0.2.4"]
                                  [figwheel-sidecar "0.5.14"]
                                  [org.clojure/test.check "0.9.0"] ;; For cljs.spec
                                  [com.cemerick/piggieback "0.2.2"]]
                   :source-paths ["env/dev"]
                   :repl-options {:init (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

  :plugins [[lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src/clj" "src/cljc"]

  :clean-targets ^{:protect false} ["resources/public/js" "target"]

  :cljsbuild {:builds [{:id "cards"
                        :source-paths ["src/cljs" "src/cljc"]
                        :figwheel {:devcards true}
                        :compiler {:main cards.core
                                   :asset-path "js/compiled/cards-out"
                                   :output-to "resources/public/js/cards.js"
                                   :output-dir "resources/public/js/compiled/cards-out"}}

                       {:id "dev"
                        :source-paths ["src/cljs" "src/cljc" "env/dev"]
                        :figwheel {:on-jsload "frontend.core/render"}
                        :compiler {:main frontend.core
                                   ;; Figwheel injects script tags for
                                   ;; development. This is the location
                                   ;; for the compiled resources
                                   :asset-path "js/compiled/out"
                                   ;; The outputted main bundle
                                   :output-to "resources/public/js/app.js"
                                   ;; Where to compile assets needed for
                                   ;; the development bundle, required by
                                   ;; :asset-path
                                   :output-dir "resources/public/js/compiled/out"}}

                       {:id "test"
                        :source-paths ["src/cljs" "src/cljc" "test/cljs" "env/test"]
                        :figwheel true
                        :compiler {:main frontend.test-runner
                                   :asset-path "js/compiled/test-out"
                                   :output-to "resources/public/js/test.js"
                                   :output-dir "resources/public/js/compiled/test-out"
                                   :optimizations :none}}

                       {:id "min"
                        :source-paths ["src/cljs" "src/cljc"]
                        :compiler {:output-to "resources/public/js/app.js"
                                   :main frontend.core
                                   :optimizations :advanced
                                   :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]})
