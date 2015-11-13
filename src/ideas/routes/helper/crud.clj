(ns ideas.routes.helper.crud
  (:require [inflections.core :as inflections]
            [ideas.util :refer [parse-int]]))

(def ^:dynamic *db-list-fn* "db/list-%s")
(def ^:dynamic *list-page* "%s/list.html")
(def ^:dynamic *db-get-fn* "db/get-%s")
(def ^:dynamic *show-page* "%s/show.html")
(def ^:dynamic *add-page* "%s/add.html")

(defmacro crud-for-list [model]
  (let [model-plural (inflections/plural model)
        db-list-symbol (symbol (format *db-list-fn* model-plural))
        list-page (format *list-page* model-plural)]
    `(fn []
       (layout/render ~list-page
         {~(keyword model-plural) (~db-list-symbol)}))))


(defmacro crud-for-show [model]
  (let [model-plural (inflections/plural model)
        db-show-symbol (symbol (format *db-get-fn* model))
        show-page (format *show-page* model-plural)]
    `(fn [id#]
       (layout/render ~show-page
         {~(keyword model) (~db-show-symbol (parse-int id#))
          }))))

(defmacro crud-for-add [model fields]
  (println fields)
  (let [model-plural (inflections/plural model)
        add-page (format *add-page* model-plural)
        default-fields (repeat (count fields) "")
        field-values (->> fields ; build a tuple2 vector
                       (map #(vector (keyword %1) %1))
                       flatten   ; ... then flat/hash map it
                       (apply hash-map))]
    (println field-values)
    `(letfn [(thisfn#
               ([] (thisfn# ~@default-fields))
               (~fields (layout/render ~add-page ~field-values)))]
       thisfn#)))