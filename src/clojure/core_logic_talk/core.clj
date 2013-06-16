
(ns core-logic-talk.core
  (:require clojure.core.logic
            [hiccup.core :refer [html]]
            [hiccup.form :as f]
            [hiccup.page :refer [include-js include-css]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.util.response :as res]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [compojure.handler :as handler]))

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

(defn index-page [req]
  (let [title "core.logic tool"]
    (html
      [:head
       [:title title]
       (include-css "/assets/codemirror/codemirror.css")
       (include-css "/assets/css/bootstrap.css")
       (include-css "/assets/css/main.css")]
      [:body
       [:div.container
        [:div.row
         [:div.span9
          (f/text-area {} :code)]]
        [:div.row
         [:div.span9
          [:div.result]
          [:div.timer]]]]
       (include-js "/assets/codemirror/codemirror.js")
       (include-js "/assets/codemirror/clojure.js")
       (include-js "/assets/js/application.js")])))

(def env '(do (require '[clojure.core.logic :refer :all])))

(defn run-code [{:keys [params]}]
  (let [code (read-string (format "(do %s)" (:code params)))
        result (eval (concat env (vector code)))]
    (edn result 200)))

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

