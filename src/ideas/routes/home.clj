(ns ideas.routes.home
  (:use compojure.core)
  (:require [ideas.views.layout :as layout]
            [ideas.util :as util]
            [prone.debug :refer [debug]]))

(defn home-page []
  (debug)
  (layout/render
    "home/index.html" {:content (util/md->html "/md/home.md")}))

(defn about-page []
  (layout/render "home/about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
