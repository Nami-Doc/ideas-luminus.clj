(ns ideas.routes.ideas
  (:use compojure.core)
  (:require [ideas.views.layout :as layout]
            [noir.session :as session]
            [noir.validation :as vali]
            [ideas.util :as util]
            [ideas.models.db :as db]
            [ideas.util :refer [parse-int]]
            [ideas.routes.helper :refer [filter-req is-auth! is-anon!]]))

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

(defn list-page []
  (layout/render
    "ideas/list.html" {:ideas (db/list-ideas)}))

(defn show-page [id]
  (layout/render
    "ideas/show.html" {:idea (db/get-idea (parse-int id))}))

(defn add-page
  ([]
    (add-page "" "" ""))
  ([name description error]
    (layout/render
      "ideas/add.html"
      {:name name
       :description description
       :error error})))

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