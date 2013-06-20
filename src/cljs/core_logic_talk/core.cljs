
(ns core-logic-talk.core
  (:require [domina :refer [value single-node]]
            [domina.css :refer [sel]]
            [enfocus.core :as ef]
            [ajax.core :as ajax]
            [lowline.functions :refer [debounce]])
  (:require-macros [enfocus.macros :as em]))

(def levels
  [{:name "Run Logic Run"
    :description "Here is a description of the first level."
    :code '(run 1 [q])
    :goal '(_0)}])

(def editor (atom nil))

(def current-level-index (atom nil))

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

(defn set-state [code]
  (.setValue
    (deref editor)
    code))

(defn init-state []
  (set-state
    (str (fetch "code"))))

;; Editor

(defn make-editor [config]
  (CodeMirror/fromTextArea
      (single-node (sel "textarea"))
      config))

(defn init-editor []
  (let [config (clj->js {:mode "clojure"
                         :matchBrackets true
                         :extraKeys {:Ctrl-E run-code}})]
    (reset!
      editor
      (doto (make-editor config)
        (.on "change" (debounce save-state))))))

;; Tutorial

(em/defaction render-level [level]
  [".intro"] (em/add-class "hide")
  [".editor"] (em/add-class "show")
  [".name"] (em/content (:name level))
  [".description"] (em/content (:description level)))

(defn get-level [index]
  (nth levels index))

(defn current-level []
  (get-level @current-level-index))

(defn show-level [index]
  (reset! current-level-index index)
  (let [level (current-level)]
    (render-level level)
    (set-state (pr-str (:code level)))))

(defn check-result [result]
  (let [goal (:goal (current-level))]
    (if (= result goal)
      (.log js/console "WIN"))))

;; UI

(em/defaction render-result [result]
  [".result"] (em/do-> (em/remove-class "error")
                       (em/content (pr-str result))))

(em/defaction render-error [result]
  [".result"] (em/do-> (em/add-class "error")
                       (em/content
                         (-> result :response :message))))

(defn show-result [result]
  (render-result result)
  (check-result result))

(defn show-error [result]
  (render-error result))

;; Init

(em/defaction init-listeners []
  ["input"] (em/listen :click run-code))

(em/defaction init-intro []
  [".intro a"] (em/listen
                 :click
                 (partial show-level 0)))

(set!
  (.-onload js/window)
  (fn []
    (init-editor)
    (init-listeners)
    (init-intro)))

