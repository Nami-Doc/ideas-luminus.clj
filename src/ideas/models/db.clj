(ns ideas.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [ideas.models.schema :as schema]))

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

(defmacro find-getter [table field & [getter-name]]
  (let [getter-name (if (nil? getter-name) (str "find-" (str table) "-by-" (str field)) getter-name)]
    ; TODO currently, the getter will have the table name in plural as that's what's given to it... :(
   `(defn ~(symbol getter-name) [~field]
     (first (select ~table
             (where {~(keyword field) ~field})
             (limit 1))))))

(find-getter users id "find-user")
(find-getter users username)
(find-getter users email)

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
