(ns ideas.routes.users
  (:require [compojure.core :refer :all]
            [ideas.models.db :as db]
            [ideas.views.layout :as layout]
            [ideas.util :refer [parse-int]]))


(defn- show-page [id]
  (if-let [user (db/find-user (parse-int id))]
    (let [implementations (db/find-implementations-by-user user)]
      (println implementations)
      (layout/render "users/show.html"
                     {:user user
                      :implementations implementations}))))

(defroutes users-routes
  (context "/users" []
           (GET "/:id" [id] (show-page id))))
