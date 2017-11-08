(ns dev.user
  (:require [frontend.core :as app]
            [devtools.core :as devtools]))

(devtools/install! [:custom-formatters :sanity-hints])

(def on-js-load app/render!)

(app/init!)
