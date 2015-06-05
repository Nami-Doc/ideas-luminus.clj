(ns ideas.util
  (:require [noir.io :as io]
            [markdown.core :as md]))

;; "ported" from scheme
(defmacro and-let
  [bindings & expr]
  (if (seq bindings)
    `(if-let [~(first bindings) ~(second bindings)]
       (and-let ~(drop 2 bindings) ~expr))
    expr))

(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (->>
    (io/slurp-resource filename)
    (md/md-to-html-string)))
