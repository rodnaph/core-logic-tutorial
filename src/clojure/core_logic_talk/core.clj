
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
         [:div.span8
          (f/text-area {} :code)
          (f/submit-button {:class "btn btn-primary"} "Abracadabra (ctrl-e)")]
         [:div.span4
          [:div.result]
          [:div.timer]]]]
       (include-js "/assets/codemirror/codemirror.js")
       (include-js "/assets/codemirror/clojure.js")
       (include-js "/assets/js/application.js")])))

(def env '(do (require '[clojure.core.logic :refer :all])))

(defn run-code [{:keys [params]}]
  (let [code (read-string (format "(do %s)" (:code params)))
        result (eval (concat env (vector code)))]
    (-> (res/response (pr-str result))
        (res/content-type "application/edn"))))

(defroutes all-routes
  (GET "/" [] index-page)
  (GET "/run" [] run-code)
  (route/resources "/assets"))

(def app (-> #'all-routes
             (wrap-stacktrace)
             (handler/site)))

(defn- start-server []
  (run-jetty app {:port 1234
                  :join? false}))

(defn -main []
  (start-server))

