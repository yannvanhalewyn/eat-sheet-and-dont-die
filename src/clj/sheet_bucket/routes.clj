(ns sheet-bucket.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET POST]]))

(def index-view
  {:status 200
   :headers {"Cache-Control" "max-age=0, private, must-revalidate"
             "Content-Type" "text/html; charset=UTF-8"}
   :body (slurp (io/resource "public/index.html"))})

(defroutes app-routes
  (GET "/" [] index-view))
