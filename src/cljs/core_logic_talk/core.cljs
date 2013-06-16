
(ns core-logic-talk.core
  (:require [domina :refer [value single-node]]
            [domina.css :refer [sel]]
            [enfocus.core :as ef]
            [ajax.core :as ajax]
            [lowline.functions :refer [debounce]])
  (:require-macros [enfocus.macros :as em]))

(def editor (atom nil))

;; Eval

(declare show-result show-error)

(defn get-code []
  (.getValue (deref editor)))

(defn run-code []
  (ajax/GET "/run"
            {:params {:code (get-code)}
             :handler show-result
             :error-handler show-error}))

;; State

(defn load-state []
  (str (aget js/localStorage "code")))

(defn save-state []
  (.log js/console "save state")
  (aset js/localStorage "code" (get-code)))

(defn init-state []
  (.setValue
    (deref editor)
    (load-state)))

;; Editor

(defn make-editor [config]
  (CodeMirror/fromTextArea
      (single-node (sel "textarea"))
      config))

(defn init-editor []
  (let [config (clj->js {:mode "clojure"
                         :tabSize 2
                         :lineNumbers false
                         :matchBrackets true})
        ed (doto (make-editor config)
             (.on "change" (debounce save-state)))]
    (reset! editor ed)))

;; UI

(em/defaction show-result [result]
  [".result"] (em/do-> (em/remove-class "error")
                       (em/content (pr-str result))))

(em/defaction show-error [{:keys [status-text]}]
  [".result"] (em/do-> (em/add-class "error")
                       (em/content status-text)))

(em/defaction init-listeners []
  ["input"] (em/listen :click run-code))

(defn init []
  (init-editor)
  (init-listeners)
  (init-state) )

(set! (.-onload js/window) init)

