(ns ideas.routes.home
  (:use compojure.core)
  (:require [ideas.views.layout :as layout]
            [ideas.util :as util]
            [ideas.models.db :as db]))

(defn list-page []
  (layout/render
    "ideas/list.html" {:ideas (db/list-ideas)}))