(ns sheet-bucket.routes
  (:require [sheet-bucket.controllers.sheets :as sheets]
            [sheet-bucket.controllers.session :as session]
            [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET PATCH]]
            [compojure.route :as route]))

(def index-view
  {:status 200
   :headers {"Cache-Control" "max-age=0, private, must-revalidate"
             "Content-Type" "text/html; charset=UTF-8"}
   :body (slurp (io/resource "public/index.html"))})

(defroutes app-routes
  (GET "/" [] index-view)
  (GET "/api/me" [] session/show)
  (GET "/api/sheets" [] sheets/index)
  (PATCH "/api/sheets/:eid" [] sheets/update)
  (route/not-found "<h1>NOT FOUND</h1>"))
