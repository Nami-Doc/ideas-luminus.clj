(ns ideas.routes.implementations
  (:use compojure.core)
  (:require [ideas.views.layout :as layout]
            [ideas.models.db :as db]
            [ideas.util :refer [parse-int]]))

(defn add-page [idea-id]
  (if-let [idea (db/get-idea (parse-int idea-id))]
    (layout/render
      "implementations/add.html"
      {:idea idea})))

(defn crud-routes [idea-id]
  (routes
    ;; @XXX I probably don't need a "list"
    (GET "/add" [] (add-page idea-id))))

(defroutes implementations-routes
  (context "/ideas/:idea-id/implementations" [idea-id]
    (crud-routes idea-id)))