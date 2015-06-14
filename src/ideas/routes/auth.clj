(ns ideas.routes.auth
  (:use compojure.core)
  (:require [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [ideas.models.db :as db]
            [prone.debug :refer [debug]]
            [ideas.views.layout :as layout]
            [ideas.util :refer [and-let]]
            [ideas.routes.helper :refer [filter-req is-auth! is-anon!]]))

(defn valid? [username email pass pass1]
  (vali/rule (vali/has-value? username)
             [:username "you must specify a username"])
  (vali/rule (vali/has-value? email)
             [:email "you must specify a email"])
  (vali/rule (not (db/find-user-by-username username))
             [:username "this username is already taken"])
  (vali/rule (vali/is-email? email)
             [:email "email field is mandatory"])
  (vali/rule (not (db/find-user-by-email email))
             [:email "this email is already taken"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 characters"])
  (vali/rule (= pass pass1)
             [:pass1 "entered passwords do not match"])
  (not (vali/errors? :username :email :pass :pass1)))


(defn- authenticate ; REFACTOR move this later (to models prolly)
  "Returns the user associated with
  the username/password combination"
  [username password]
  (if-let [user (db/find-user-by-username username)]
    (if (crypt/compare password (:pass user))
      user
      nil)))

(defn handle-login [username pass]
  (if-let [user (authenticate username pass)]
    (do
      (session/put! :user-id (:id user))
      (session/flash-put! :notice "Logged in successfully!"))
    (session/flash-put! :error "User not found!"))
  (resp/redirect "/"))


(defn register []
  (layout/render
    "auth/registration.html"
    {:username-error (vali/on-error :username first)
     :pass-error (vali/on-error :pass first)
     :pass1-error (vali/on-error :pass1 first)}))

(defn handle-registration
  [username email pass pass1]
  (if (valid? username email pass pass1)
    (try
      (let [user (db/create-user {:username username :email email :pass (crypt/encrypt pass)})]
        (session/put! :user-id (:id user))
        (session/flash-put! :notice "Welcome on ideas!")
        (resp/redirect "/profile"))
      (catch Exception ex
        (vali/rule false [:id (.getMessage ex)])
        (register)))
    (do
      (session/flash-put! :error "Invalid user")
      (register))))

(defn profile []
  (if-let [user (db/find-user (session/get :user-id))]
    (layout/render
      "auth/profile.html"
      {:user user})))

(defn update-profile
  [{:keys [first-name last-name email]}]
  (if (not (nil? (session/get :user-id)))
    (do ;; if we're logged in, proceed
      (db/update-user (session/get :user-id) first-name last-name email)
      (profile))
    (resp/redirect "/")))

(defn logout []
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (POST "/login" [username pass]
    (is-anon! #(handle-login username pass)))

  (GET "/register" []
    (is-anon! register))

  (POST "/register" [username email pass pass1]
    (is-anon! #(handle-registration username email pass pass1)))

  (GET "/profile" []
    (is-auth! profile))

  (POST "/update-profile" {params :params}
    (is-auth! #(update-profile params)))

  (GET "/logout" []
    (is-auth! logout)))
