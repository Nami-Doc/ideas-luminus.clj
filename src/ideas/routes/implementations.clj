(ns ideas.routes.implementations
  (:use compojure.core)
  (:require [ideas.models.db :as db]
            [ideas.routes.helper.crud :refer (crud-for-list crud-for-add)]
            [ideas.routes.helper.request :refer [is-auth!]]
            [ideas.util :refer [parse-int]]
            [ideas.views.layout :as layout]
            [noir.response :as resp]
            [noir.session :as session]
            [noir.validation :as vali]))

(def list-page (crud-for-list "implementation"))
(defn add-page
  ([idea] (add-page idea "" ""))
  ([idea name url] (layout/render "implementations/add.html"
                                  {:idea idea :name name :url url})))

(defn- valid? [name url]
  true)

(defn save-page [idea name url]
  (if (valid? name url)
    (do
      (db/create-implementation {:name name :url url :idea_id (:id idea)})
      (list-page))
    (do
      (session/flash-put! :error "Invalid implementation")
      (add-page name url "Invalid implementation"))))

(defn crud-routes [idea-id]
  (if-let [idea (db/get-idea (parse-int idea-id))]
    (routes
     (GET "/add" []
          (is-auth! #(add-page idea)))
     (POST "/" [name url]
           (is-auth! #(save-page idea name url)))
     )))

(defroutes implementations-routes
  (context "/ideas/:idea-id/implementations" [idea-id]
    (crud-routes idea-id)))
