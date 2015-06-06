(ns ideas.util
  (:require [noir.io :as io]
            [markdown.core :as md]))

;; "ported" from scheme
(defn- and-let-impl
  [bindings expr]
  (if (seq bindings)
    `(if-let [~(first bindings) ~(second bindings)]
       ~(and-let-impl (drop 2 bindings) expr))
    `(do ~@expr)))

(defmacro and-let
  [bindings & expr]
  (and-let-impl bindings expr))

(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (->>
    (io/slurp-resource filename)
    (md/md-to-html-string)))
