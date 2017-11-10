(ns dev.user
  (:require [frontend.core :as app]
            [devtools.core :as devtools]
            [re-frame.loggers :as rf-loggers]))

(devtools/install! [:custom-formatters :sanity-hints])

(def warn #(.warn js/console %))

;; Hide re-frame warnings like:
;;   "re-frame: overwriting :event handler for: :subscription"
;; when figwheel is reloading code
(rf-loggers/set-loggers!
  {:warn (fn [& args]
           (when-not (= "re-frame: overwriting" (first args))
             (apply warn args)))})

(def on-js-load app/render!)

(defonce _ (app/init!))
