
(ns core-logic-talk.core
  (:require [domina :refer [value single-node]]
            [domina.css :refer [sel]]
            [enfocus.core :as ef]
            [ajax.core :as ajax]
            [lowline.functions :refer [debounce]])
  (:require-macros [enfocus.macros :as em]))

(def editor (atom nil))

(def countdown (atom nil))

;; Eval

(declare show-result show-error)

(defn get-code []
  (.getValue (deref editor)))

(defn run-code []
  (ajax/GET "/run"
            {:params {:code (get-code)}
             :handler show-result
             :error-handler show-error}))

;; Local Storage

(defn fetch [k]
  (aget js/localStorage k))

(defn store [k v]
  (aset js/localStorage k v))

;; State

(defn save-state []
  (store
    "code"
    (get-code)))

(defn init-state []
  (.setValue
    (deref editor)
    (str (fetch "code"))))

;; Editor

(defn make-editor [config]
  (CodeMirror/fromTextArea
      (single-node (sel "textarea"))
      config))

(defn init-editor []
  (let [config (clj->js {:mode "clojure"
                         :lineNumbers false
                         :matchBrackets true
                         :extraKeys {:Ctrl-E run-code}})
        ed (doto (make-editor config)
             (.on "change" (debounce save-state)))]
    (reset! editor ed)))

;; Timer

(declare update-timer)

(defn tick-timer []
  (update-timer)
  (swap! countdown dec))

(defn init-timer []
  (reset! countdown 300)
  (js/setInterval tick-timer 1000))

;; UI

(em/defaction update-timer []
  [".timer"] (em/content (str (deref countdown))))

(em/defaction show-result [result]
  [".result"] (em/do-> (em/remove-class "error")
                       (em/content (pr-str result))))

(em/defaction show-error [{:keys [status-text]}]
  [".result"] (em/do-> (em/add-class "error")
                       (em/content status-text)))

(em/defaction init-listeners []
  ["input"] (em/listen :click run-code))

;; Init

(set!
  (.-onload js/window)
  (fn []
    (init-timer)
    (init-editor)
    (init-listeners)
    (init-state)))

