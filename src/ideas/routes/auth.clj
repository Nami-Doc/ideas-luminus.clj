(ns ideas.routes.auth
  (:use compojure.core)
  (:require [ideas.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [ideas.models.db :as db]
            [prone.debug :refer [debug]]))

(defn valid? [username email pass pass1]
  (vali/rule (vali/has-value? username)
             [:username "you must specify a username"])
  (vali/rule (not (db/find-user username))
             [:username "this username is already taken"])
  (vali/rule (not (db/find-user-by-email email))
             [:email "this email is already taken"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 characters"])
  (vali/rule (= pass pass1)
             [:pass1 "entered passwords do not match"])
  (not (vali/errors? :id :pass :pass1)))

(defn register []
  (layout/render
    "auth/registration.html"
    {:username-error (vali/on-error :username first)
     :pass-error (vali/on-error :pass first)
     :pass1-error (vali/on-error :pass1 first)}))

(defn handle-registration [username email pass pass1]
  (if (valid? username email pass pass1)
    (try
      (do
        (let [user (db/create-user {:username username :email email :pass (crypt/encrypt pass)})]
          (session/put! :user-id (:id user))
          (resp/redirect "/profile")))
      (catch Exception ex
        (vali/rule false [:id (.getMessage ex)])
        (register)))))

(defn profile []
  (and-let [user-id (session/get :user-id)
            user (db/find-user user-id)]
    (layout/render
      "auth/profile.html"
      {:user user})
    (resp/redirect "/")))

(defn update-profile [{:keys [first-name last-name email]}]
  (if (not (nil? (session/get :user-id)))
    (do ;; if we're logged in, proceed
      (db/update-user (session/get :user-id) first-name last-name email)
      (profile))
    (resp/redirect "/")))

(defn handle-login [username pass]
  (if (nil? (session/get :user-id))
    (let [user (db/find-user-by-username username)]
      (when (and user (crypt/compare pass (:pass user)))
        (session/put! :user-id (:id user))))
    (resp/redirect "/"))) ;; redirect either way

(defn logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (GET "/register" []
       (register))

  (POST "/register" [username email pass pass1]
        (handle-registration username email pass pass1))

  (GET "/profile" [] (profile))

  (POST "/update-profile" {params :params} (update-profile params))

  (POST "/login" [username pass]
        (handle-login username pass))

  (GET "/logout" []
        (logout)))
