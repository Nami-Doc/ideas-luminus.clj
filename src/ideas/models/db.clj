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
    `(defn ~(symbol getter) [~field]
       (first (select ~table
                      (where {~(keyword field) ~field})
                      (limit 1))))))


;; ok, now to the actual DB things...
(defdb db schema/db-spec)
(declare users implementations ideas)

;; users
(defentity users)

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

;; implementations
(defentity implementations
  (belongs-to ideas)
  (belongs-to users {:fk :user_id}))

(defn list-implementations []
  (select implementations))

;(generate-find implementations id)
(defn find-implementation [id]
  (first (select implementations
                 ;(with idea)
                 (with users)
                 (where {:id id}))))

(defn create-implementation [impl]
  (insert implementations
          (values impl)))

(defn find-implementations-by-user [user]
  (select implementations
          (with ideas)
          (where (= :user_id (:id user)))))

;; ideas
(defentity ideas
  (has-many implementations {:fk :idea_id}))

(defn list-ideas []
  (select ideas))

(defn find-idea [id]
  (first (select ideas
                 (with implementations)
                 (where {:id id})
                 (limit 1))))

(defn create-idea [idea]
  (insert ideas
          (values idea)))
