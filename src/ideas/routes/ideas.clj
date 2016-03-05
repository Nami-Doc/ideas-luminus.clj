(ns ideas.routes.ideas
  (:use compojure.core)
  (:require [ideas.views.layout :as layout]
            [noir.session :as session]
            [noir.validation :as vali]
            [ideas.util :as util]
            [ideas.models.db :as db]
            [ideas.routes.helper.request :refer [is-auth!]]
            [ideas.routes.helper.crud :refer [crud-for-list crud-for-show crud-for-add]]))

(defn valid? [name description]
  (vali/rule (vali/has-value? name)
             [:name "you must specify a name"])
  (vali/rule (vali/has-value? description)
             [:description "you must specify a description"])
  (vali/rule (vali/min-length? description 5)
             [:description "your description must be at least 5 characters"])
  ;; TODO check that the idea's name is not taken yet
  ;;      (will require case-insensitive comparison, probably)
  (not (vali/errors? :name :description)))

(def list-page (crud-for-list "idea"))
(def show-page (crud-for-show "idea"))
(def add-page (crud-for-add "idea" [name description error]))

(defn save-page [name description]
  (if (valid? name description)
    (do
      (db/create-idea {:name name :description description})
      (list-page))
    (do
      (session/flash-put! :error "Invalid idea")
      (add-page name description "Invalid idea"))))

(defroutes crud-routes
  ;; @TODO list-page should probably have a :category-id parameter
  (GET "/" [] (list-page))
  (GET "/add" [] (is-auth! add-page))
  (GET "/:id" [id] (show-page id))
  (POST "/" [name description]
    (is-auth! #(save-page name description))))

(defroutes ideas-routes
  (context "/ideas" [] crud-routes))
