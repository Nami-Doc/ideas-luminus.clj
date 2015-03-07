(defproject
  ideas
  "0.1.0-SNAPSHOT"

  :repl-options
  {:init-ns ideas.repl}

  :dependencies
  [
   [org.clojure/clojure "1.6.0"]
   [ring-server "0.4.0"] ;; web server
   [ragtime "0.3.8"] ;; migration stuff
   [environ "1.0.0"] ;; manage envs (TODO use it)
   [markdown-clj "0.9.62"] ;; used for resources/md/
   [com.taoensso/timbre "3.4.0"] ;; logging / profiling
   [korma "0.4.0"]
   [com.taoensso/tower "3.0.2"] ;; i18n
   [selmer "0.8.0"] ;; jinja-like templating
   [lib-noir "0.9.5"] ;; ring helpers: sessions, assets, input val., caching, ...
   [compojure "1.3.2"] ;; routing
   [postgresql/postgresql "9.1-901.jdbc4"]
   [log4j "1.2.17" ;; logging. seems to be outdated (2012)
    :exclusions [javax.mail/mail javax.jms/jms com.sun.jdmk/jmxtools com.sun.jmx/jmxri]]

   ;; cljs
   [org.clojure/clojurescript "0.0-2913"]
   [domina "1.0.3"] ;; DOM
   [hipo "0.3.0"] ;; templating
   [prismatic/dommy "1.0.0"] ;; event
   [cljs-ajax "0.3.10"] ;; ajax

   ; and now, for project-specific deps...
   [inflections "0.9.13"] ; used for DB stuff
   [prone "0.8.1"] ; better_errors for ring
   ]

  :source-paths
  ["src"]

  :cljsbuild
  {:builds
   [{:source-paths ["src-cljs"],
     :compiler
     {:pretty-print false,
      :output-to "resources/public/js/site.js",
      :optimizations :advanced}}]}

  :ring
  {:handler ideas.handler/app,
   :init ideas.handler/init,
   :destroy ideas.handler/destroy}

  :ragtime
  {:migrations ragtime.sql.files/migrations,
   :database
   "jdbc:postgresql://localhost/ideas?user=ideas_user&password=ideas-password"}

  :profiles
  {
   :uberjar
   {:aot :all},
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}},
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.2.2"]],
    :env {:dev true}
    :ring {:auto-reload? true
           :stracktrace-middleware prone.middleware/wrap-exceptions}}}

  :url
  "https://github.com/vendethiel/ideas-luminus.clj"

  :plugins
  [[lein-ring "0.9.2"]
   [lein-environ "0.4.0"]
   [ragtime/ragtime.lein "0.3.4"]
   [lein-cljsbuild "0.3.3"]]

  :description
  "Tell us what are your ideas!"

  :min-lein-version
  "2.0.0")