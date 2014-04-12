(ns ideas.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [ideas.models.schema :as schema]))

(defdb db schema/db-spec)

(defentity users)

(defentity ideas)

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

(defn list-ideas []
  (select ideas))