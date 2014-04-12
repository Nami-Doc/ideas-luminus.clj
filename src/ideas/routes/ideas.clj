(ns ideas.routes.ideas
  (:use compojure.core)
  (:require [ideas.views.layout :as layout]
            [ideas.util :as util]
            [ideas.models.db :as db]))

(defn list-page []
  (layout/render
    "ideas/list.html" {:ideas (db/list-ideas)}))

(defn show-page [id]
  (layout/render
    "ideas/show.html" {:idea (db/get-idea id)}))

(defroutes crud-routes
  (GET "/" [] (list-page))
  (GET "/:id" [id] (show-page)))

(defroutes ideas-routes
  (context "/ideas" [] crud-routes))