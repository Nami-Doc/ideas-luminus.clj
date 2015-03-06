(ns ideas.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [ideas.models.schema :as schema]
            [inflections.core :as inflections]))

;; so meta
(defmacro generate-find [table field & [getter-name]]
          (let [table-name (inflections/singular (str table))
                getter (if (= "id" (str field))
                         (str "find-" table-name)
                         (or getter-name (str "find-" table-name "-by-" (str field))))]
               (println getter)
               ; TODO currently, the getter will have the table name in plural as that's what's given to it... :(
               `(defn ~(symbol getter) [~field]
                      (first (select ~table
                                     (where {~(keyword field) ~field})
                                     (limit 1))))))


;; ok, now to the actual DB things...
(defdb db schema/db-spec)

(defentity users)

;; users
(defn create-user [user]
  (insert users
          (values user)))

(defn update-user [id first-name last-name email]
  (update users
  (set-fields {:first_name first-name
               :last_name last-name
               :email email})
  (where {:id id})))

(generate-find users id)
(generate-find users username)
(generate-find users email)

;; ideas
(defentity ideas)

(defn list-ideas []
  (select ideas))

(defn get-idea [id]
  (first (select ideas
                 (where {:id id})
                 (limit 1))))

(defn create-idea [idea]
  (insert ideas
          (values idea)))
