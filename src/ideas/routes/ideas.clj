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

(defn add-page [& [name description error]]
  (layout/render
    "ideas/add.html" {:name name
                      :description description
                      :error error}))

(defn save-page [name description]
  (cond
    (empty? name)
    (add-page name description "The idea must be named !")
    
    (empty? description)
    (add-page name description "The idea must be described !")

    :else
    (do
      (db/save-idea name description)
      (list-page))))

(defroutes crud-routes
  ;; @TODO list-page should probably have a :category-id parameter
  (GET "/" [] (list-page))
  (GET "/:id" [id] (show-page))
  (POST "/" [name description] (save-page name description)))

(defroutes ideas-routes
  (context "/ideas" [] crud-routes))