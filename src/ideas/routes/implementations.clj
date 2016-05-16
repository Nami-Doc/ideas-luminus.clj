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
(def add-page (crud-for-add "implementation" [name url]))

(defn- valid? [name url]
  true)

(defn save-page [name url]
  (if (valid? name url)
    (do
      (db/create-implementation [name url])
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
           (is-auth! #(save-page name url))))))

(defroutes implementations-routes
  (context "/ideas/:idea-id/implementations" [idea-id]
    (crud-routes idea-id)))
