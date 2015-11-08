(ns ideas.routes.implementations
  (:use compojure.core)
  (:require [index.views.layout :as layout]))

(def crud-routes [idea-id]
  (routes
    ;; @XXX I probably don't need a "list"
    (GET "/add")))

(defroutes implementations-routes
  (context "/ideas/:idea-id/implementations" [idea-id]
    (crud-routes idea-id)))