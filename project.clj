(defproject sheet-bucket "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.6.1"
  
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [org.clojure/core.async "0.2.374"
                  :exclusions [org.clojure/tools.reader]]
                 [reagent "0.5.1"]]
  
  :plugins [[lein-figwheel "0.5.3-1"]
            [lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main sheet-bucket.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/sheet_bucket.js"
                                   :output-dir "resources/public/js/compiled/out"}}

                       {:id "min"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/compiled/sheet_bucket.js"
                                   :main sheet-bucket.core
                                   :optimizations :advanced
                                   :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"]}
  
  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.3-1"]                                   
                                  [com.cemerick/piggieback "0.2.1"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:init (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
