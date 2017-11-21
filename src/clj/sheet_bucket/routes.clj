(ns sheet-bucket.routes
  (:require [sheet-bucket.controllers.sheets :as sheets]
            [sheet-bucket.controllers.session :as session]
            [clojure.java.io :as io]
            [compojure.core :as comp :refer [defroutes GET PATCH POST DELETE]]
            [compojure.route :as route]))

(def index-view
  {:status 200
   :headers {"Cache-Control" "max-age=0, private, must-revalidate"
             "Content-Type" "text/html; charset=UTF-8"}
   :body (slurp (io/resource "public/index.html"))})

(defroutes app-routes
  (GET "/" [] index-view)
  (route/not-found "<h1>NOT FOUND</h1>"))

(defn wrap-chsk-routes [routes chsk]
  (comp/routes
    (GET "/chsk" [] (:ajax-get-or-ws-handshake-fn chsk))
    (POST "/chsk" [] (:ajax-post-fn chsk))
    routes))
