
(ns core-logic-talk.core
  (:require [domina :refer [value single-node]]
            [domina.css :refer [sel]]
            [enfocus.core :as ef]
            [ajax.core :as ajax])
  (:require-macros [enfocus.macros :as em]))

(def editor (atom nil))

(em/defaction show-result [result]
  [".result"] (em/do-> (em/remove-class "error")
                       (em/content (pr-str result))))

(em/defaction show-error [{:keys [status-text]}]
  [".result"] (em/do-> (em/add-class "error")
                       (em/content status-text)))

(defn run-code []
  (let [code (.getValue (deref editor))]
    (ajax/GET "/run"
              {:params {:code code}
               :handler show-result
               :error-handler show-error})))

(em/defaction init-listeners []
  ["input"] (em/listen :click run-code))

(defn make-editor [config]
  (CodeMirror/fromTextArea
      (single-node (sel "textarea"))
      config))

(defn init-editor []
  (let [config (clj->js {:mode "clojure"
                         :lineNumbers false
                         :matchBrackets true})]
    (reset! editor (make-editor config))))

(defn init []
  (init-editor)
  (init-listeners))

(set! (.-onload js/window) init)

