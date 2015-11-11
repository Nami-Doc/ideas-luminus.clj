(ns ideas.routes.implementations
  (:use compojure.core)
  (:require [noir.response :as resp]
            [noir.validation :as vali]
            [ideas.views.layout :as layout]
            [ideas.models.db :as db]
            [ideas.util :refer [parse-int]]))

(defn- valid? [my fields]
  )

(defn add-page [idea]
  (layout/render
    "implementations/add.html"
    {:idea idea}))

(defn save-page [idea fields]
  (if (valid? fields)
    (do
      (save fields)
      (resp/redirect (str "ideas/" (:id idea))))
    (do
      )))

(defn crud-routes [idea-id]
  (if-let [idea (db/get-idea (parse-int idea-id))]
    (routes
      (GET "/add" []
        (is-auth! #(add-page idea)))
      (POST "/" [my fields]
        (is-auth! #(save-page my fields))))))

(defroutes implementations-routes
  (context "/ideas/:idea-id/implementations" [idea-id]
    (crud-routes idea-id)))