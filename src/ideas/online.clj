(ns ideas.online
  (:require [noir.session :as session]
            [clj-time.core :as t]))

;; just inspect the online list with @
;; TODO maybe expose a function for that,
;;      that'll also clean up the list?
(def online (agent {}))
(def ^:dynamic *online-time* (t/minutes 5))

(defn- update-agent
  "First removes the users that havn't been active
  in the last *activ-time* seconds, then
  adds the passed user to the online list.
  (time is passed in just in case the send retries)"
  [online-users user-id time]
  (->>
    online-users
    (filter #(t/before? (t/ago *online-time*) (second %)))
    (conj {user-id time})))

(defn update-online-list
  "middleware to update the online list"
  [handler]
  (fn [req]
    (if false (session/get :user-id)
      (send online update-agent (session/get :user-id) (t/now)))
    (handler req)))