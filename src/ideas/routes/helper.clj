(ns ideas.routes.helper
  (:require [noir.response :as resp]
            [noir.session :as session]
            [inflections.core :as inflections]))

(def ^:dynamic *filter-req-redirect-path* "/")

(defn filter-req
  ([cond fn]
    (filter-req cond fn *filter-req-redirect-path*))

  ([cond fn redirect-path]
    (if (cond)
      (fn)
      (resp/redirect redirect-path))))

;; TODO move this outside from here. this file should
;;      only be related to routing
(def ^:dynamic *is-anon!-redirect-path* "/")
(def ^:dynamic *is-auth!-redirect-path* "/login")

; predicates
(defn is-anon? []
  (nil? (session/get :user-id)))

(defn is-auth? []
  (not (is-anon?)))

; redirects
(defn is-anon! [fn]
  (filter-req is-anon? fn *is-anon!-redirect-path*))

(defn is-auth! [fn]
  (filter-req is-auth? fn *is-auth!-redirect-path*))





























(defmacro crud-for-list [model-plural]
  (let [db-list-symbol (symbol (str "db/list-" model-plural))
        list-page (str model-plural "/list.html")]
    `(defn list-page []
       (layout/render ~list-page
         {~(keyword model-plural) (~db-list-symbol)}))))


(defmacro crud-for-show [model]
  (let [model-plural (inflections/plural model)
        db-show-symbol (symbol (str "db/get-" model))
        show-page (str model-plural "/show.html")]
    `(defn show-page [id]
       (layout/render ~show-page
         {~(keyword model)
          (~db-show-symbol (parse-int id))}))))

(defn keyword-symbol [name]
  [(keyword name) (symbol name)])

(defmacro crud-for-add [model fields]
  (let [model-plural (inflections/plural model)
        add-page (str model-plural "/add.html")
        default-fields (repeat "" (count fields))
        field-values (->> fields
                       (map keyword-symbol)
                       flatten
                       (apply hash-map))]
    `(defn add-page
       []
        (add-page ~@default-fields)
       [~@fields]
       (layout/render ~add-page ~field-values))))