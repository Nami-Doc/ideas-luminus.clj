(ns ideas.routes.implementations
  (:require [compojure.core :refer :all]
            [ideas.models.db :as db]
            [ideas.routes.helper
             [crud :refer [crud-for-list]]
             [request :refer [is-auth!]]]
            [ideas.routes.ideas :as ideas]
            [ideas.util :refer [parse-int]]
            [ideas.views.layout :as layout]
            [noir
             [session :as session]
             [validation :as vali]]))

(defn- params
  [idea repo_url demo_url comment]
  {:idea idea :repo_url repo_url :demo_url demo_url :comment comment})

(defn add-page
  ([idea] (add-page idea "" "" ""))
  ([idea repo_url demo_url comment]
   (layout/render "implementations/add.html"
                  (into {:errors (vali/get-errors)}
                        (params idea repo_url demo_url comment)))))

(defn- valid? [repo_url demo_url comment]
  ;; TODO assert that the repo_url is an URL (and using bitbucket / github / ...)
  (println "repo")
  (println repo_url)
  (vali/rule (vali/has-value? repo_url)
             [:repo_url "you must specify a repository url"])
  ; XXX is a demo_url required?
  ;; (vali/rule (vali/has-value? demo_url)
  ;;            [:description "you must specify a demo_url url"])
  ; XXX is a comment required?
  ;; (vali/rule (vali/min-length? description 5)
  ;;            [:description "your description must be at least 5 characters"])
  (not (vali/errors? :repo_url ;:demo_url :comment
                     )))

(defn save-page [idea repo_url demo_url comment]
  (if (valid? repo_url demo_url comment)
    (do
      ;; TODO can use `params` here? or is `idea` a no-go for the ORM?
      ; (i.e.: do I need to pass the object explicitly? can I just pass the id? etc
      (db/create-implementation {:repo_url repo_url :demo_url demo_url
                                 :user_id (session/get :user-id) :idea_id (:id idea)})
      (ideas/show-page (:id idea)))
    (do
      (session/flash-put! :error "Invalid implementation")
      (add-page idea repo_url demo_url comment))))

(defn crud-routes [idea-id]
  (if-let [idea (db/get-idea (parse-int idea-id))]
    (routes
     (GET "/add" []
          (is-auth! #(add-page idea)))
     (POST "/" [repo_url demo_url comment]
           (is-auth! #(save-page idea repo_url demo_url comment)))
     )))

(defroutes implementations-routes
  (context "/ideas/:idea-id/implementations" [idea-id]
    (crud-routes idea-id)))
