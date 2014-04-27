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

(defn get-user [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))

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