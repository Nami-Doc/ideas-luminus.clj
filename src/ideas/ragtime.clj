(ns ideas.ragtime
  (:require [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]))

(defn load-config []
  {:datastore  (jdbc/sql-database "jdbc:postgresql://localhost/ideas?user=ideas_user&password=mdr-password")
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))
