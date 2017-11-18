(ns frontend.router
  (:import goog.History)
  (:require [bidi.bidi :as bidi]
            [re-frame.core :refer [dispatch]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [clojure.string :as str]))

;; Routes and matchers
;; ===================

(def ROUTES [{:name :route/index
              :path ""}
             {:name :route/sheets
              :path "sheets/"}
             {:name :route/sheet
              :path ["sheets/" :sheet/id]} ])

(def BIDI_ROUTES
  ["" (reduce
        (fn [out {:keys [path name]}] (assoc out path name))
        {} ROUTES)])

(def strip-hash #(str/replace % #"^#" ""))
(def prepend-hash #(str "#" %))

(defn match-route [path]
  (if-let [{:keys [route-params handler]}
           (bidi/match-route BIDI_ROUTES (strip-hash path))]
    {:route/handler handler
     :route/params route-params}))

(defn path-for
  "Takes an object with :handler and :params, and returns a string
  representing the path for that route."
  [{:keys [:route/handler :route/params]}]
  (prepend-hash
    (apply bidi/path-for BIDI_ROUTES handler (flatten (into [] params)))))

;; Browser sync
;; ============

(defn- redirect-to
  "Update the javascript window.location.hash."
  [path]
  (set! (.. js/window -location -hash) path))

(defn- sync-url []
  (if-let [match (match-route (.. js/window -location -hash))]
    (dispatch [:route/browser-url match])))

(defn listen
  "Starts a watcher on the browser url and enables browser
  history. Will launch an update route action when browser url is
  submitted to change."
  []
  (let [h (History.)]
    (events/listen h EventType/NAVIGATE sync-url)
    (.setEnabled h true)))

;; Path utils
;; ==========

(defn index [] {:route/handler :route/index})
(defn sheets [] {:route/handler :route/sheets})
(defn sheet [sheet-id] {:route/handler :route/sheet
                        :route/params {:sheet/id sheet-id}})
