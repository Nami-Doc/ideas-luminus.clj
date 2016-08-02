(ns ideas.routes.helper.crud
  (:require [inflections.core :as inflections]
            [ideas.util :refer [parse-int]]))

(def ^:dynamic *db-list-fn* "db/list-%s")
(def ^:dynamic *list-page* "%s/list.html")
(def ^:dynamic *db-getter-fn* "db/find-%s")
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
        db-show-symbol (symbol (format *db-getter-fn* model))
        show-page (format *show-page* model-plural)]
    `(fn [id#]
       (if-let [record# (~db-show-symbol (parse-int id#))]
         (layout/render ~show-page
                        {~(keyword model) record#})))))

(def fields-to-hash
  (comp (partial apply hash-map)
        flatten
        (partial map #(vector (keyword %1) %1))))

(defmacro crud-for-add [model fields]
  (let [model-plural (inflections/plural model)
        add-page (format *add-page* model-plural)
        default-fields (repeat (count fields) "")
        field-values (fields-to-hash fields)]
    `(letfn [(thisfn#
               ([] (thisfn# ~@default-fields))
               (~fields (layout/render ~add-page ~field-values)))]
       thisfn#)))
