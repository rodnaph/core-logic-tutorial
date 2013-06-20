
(ns core-logic-talk.core
  (:require [hiccup.core :refer [html]]
            [hiccup.form :as f]
            [hiccup.page :refer [include-js include-css]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.util.response :as res]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [net.cgrand.enlive-html :refer [deftemplate]]))

(defn edn [data status]
  (-> (res/response (pr-str data))
      (res/status status)
      (res/content-type "application/edn")))

(defn wrap-exceptions [handler]
  (fn [req]
    (try
      (handler req)
      (catch Exception e
        (edn {:message (.getMessage e)} 500)))))

(deftemplate index-page "index.html" [req])

(defn run-code [{:keys [params]}]
  (let [code (read-string (format "(do %s)" (:code params)))
        imp '(require '[clojure.core.logic :refer :all])
        env (list 'let '[_ (gensym)] imp code)]
    (edn (eval env) 200)))

(defroutes all-routes
  (GET "/" [] index-page)
  (GET "/run" [] run-code)
  (route/resources "/assets"))

(def app (-> #'all-routes
             (wrap-exceptions)
             (handler/site)))

(defn- start-server []
  (run-jetty app {:port 1234
                  :join? false}))

(defn -main []
  (start-server))

