(ns frontend.socket
  (:require [taoensso.sente :as sente]
            [re-frame.core :refer [dispatch]]))

(def TIMEOUT 1000)
(defonce chsk (atom nil))

(defn- send! [msg cb] ((:send-fn @chsk) msg TIMEOUT cb))

(defn- event-handler [{:keys [event]}] (dispatch event))

(defn start []
  (let [_chsk (sente/make-channel-socket! "/chsk" {:type :auto})
        stop-fn (sente/start-chsk-router! (:ch-recv _chsk) event-handler)]
    (reset! chsk (assoc _chsk :stop-fn stop-fn))))

(defn sock-fx [messages]
  (doseq [[key msg] messages]
    (dispatch [(keyword "request" key) msg])
    (send! msg #(if (= 200 (:status %))
                  (dispatch [(keyword "response" key) (:body %)])
                  (dispatch [(keyword "response.failure" key) (:body %)])))))
