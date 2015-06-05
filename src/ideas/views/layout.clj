(ns ideas.views.layout
  (:require [selmer.parser :as parser]
            [clojure.string :as s]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]
            [noir.session :as session]))

(def template-path "ideas/views/templates/")

(deftype
  RenderableTemplate
  [template params]
  Renderable
  (render
    [this request]
    (content-type
      (->>
        (assoc
          params
          (keyword (s/replace template #".html" "-selected"))
          "active"
          :servlet-context
          (:context request)
          :user-id
          (session/get :user-id)
          :flash-error
          (session/flash-get :error)
          :flash-notice
          (session/flash-get :notice))
        (parser/render-file (str template-path template))
        response)
      "text/html; charset=utf-8")))

(defn render [template & [params]]
  (RenderableTemplate. template params))

