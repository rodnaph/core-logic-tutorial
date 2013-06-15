
(ns core-logic-talk.core
  (:require [domina :refer [value]]
            [domina.css :as css]
            [enfocus.core :as ef]
            [ajax.core :as ajax])
  (:require-macros [enfocus.macros :as em]))

(em/defaction show-result [result]
  [".result"] (em/content (pr-str result)))

(defn run-code []
  (let [code (value (css/sel "textarea"))]
    (ajax/GET "/run"
              {:params {:code code}
               :handler show-result})))

(em/defaction init []
  ["input"] (em/listen :click run-code))

(set! (.-onload js/window) init)

