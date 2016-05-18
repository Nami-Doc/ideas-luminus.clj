(ns ideas.routes.implementations
  (:require [compojure.core :refer :all]
            [ideas.models.db :as db]
            [ideas.routes.helper
             [crud :refer [crud-for-list]]
             [request :refer [is-auth!]]]
            [ideas.util :refer [parse-int]]
            [ideas.views.layout :as layout]
            [noir.session :as session]
            [noir.validation :as vali]))

(def list-page (crud-for-list "implementation"))
(defn- params
  [idea repo demo comment]
  {:idea idea :repo repo :demo demo :comment comment})

(defn add-page
  ([idea] (add-page idea "" ""))
  ([idea repo demo comment]
   (layout/render "implementations/add.html"
                  (params idea repo demo comment))))

(defn- valid? [repo demo comment]
  ;; TODO assert that the repo is an URL
  (vali/rule (vali/has-value? repo)
             [:repo"you must specify a repository url"])
  ; XXX is a demo required?
  ;; (vali/rule (vali/has-value? demo)
  ;;            [:description "you must specify a demo url"])
  ; XXX is a comment required?
  ;; (vali/rule (vali/min-length? description 5)
  ;;            [:description "your description must be at least 5 characters"])
  (not (vali/errors? repo demo comment)))

(defn save-page [idea repo demo comment]
  (if (valid? repo demo comment)
    (do
      (session/flash-put! :error "Invalid implementation")
      (add-page name url "Invalid implementation"))
    (do
      ;; TODO can use `params` here? or is `idea` a no-go for the ORM?
      (db/create-implementation {:name name :url url :idea_id (:id idea)})
      (list-page))))

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
