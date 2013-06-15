
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
            [compojure.handler :as handler]
            [clojail.core :refer [sandbox]]
            [clojail.testers :refer [secure-tester]]))

(defn index-page [req]
  (let [title "MAGIC PROGRAMMING"]
    (html
      [:head
       [:title title]
       (include-css "/assets/codemirror/codemirror.css")
       (include-css "/assets/css/bootstrap.css")
       (include-css "/assets/css/main.css")]
      [:body
       [:div.container
        [:div.row
         [:div.span12
          [:h1 title]]]
        [:div.row
         [:div.span3.key]
         [:div.span6
          (f/text-area {} :code)
          (f/submit-button "Abracadabra")]
         [:div.span3.result]]]
       (include-js "/assets/codemirror/codemirror.js")
       (include-js "/assets/js/application.js")])))

(def env '(do (require '[clojure.core.logic :refer
                         [run run* == fresh conde conso appendo membero]])))

(defn run-code [{:keys [params]}]
  (let [code (read-string (:code params))
        sb (sandbox secure-tester)
        result (sb (concat env (vector code)))]
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

