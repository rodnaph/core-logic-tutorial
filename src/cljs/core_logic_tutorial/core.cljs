
(ns core-logic-tutorial.core
  (:require [core-logic-tutorial.data :refer [levels]]
            [domina :refer [value single-node]]
            [domina.css :refer [sel]]
            [enfocus.core :as ef]
            [ajax.core :as ajax]
            [lowline.functions :refer [debounce]])
  (:require-macros [enfocus.macros :as em]))

(def editor (atom nil))

(def current-level-index (atom 0))

;; Eval

(declare show-result show-error)

(defn get-code []
  (.getValue (deref editor)))

(defn run-code []
  (ajax/GET "/run"
            {:params {:code (get-code)}
             :handler show-result
             :error-handler show-error}))

(defn set-state [code]
  (.setValue @editor code)
  (.autoFormatRange
    @editor
    (.getCursor @editor true)
    (.getCursor @editor false)))

;; Editor

(defn make-editor [config]
  (CodeMirror/fromTextArea
      (single-node (sel "textarea"))
      config))

(defn init-editor []
  (let [config (clj->js {:mode "clojure"
                         :matchBrackets true
                         :extraKeys {:Ctrl-E run-code}})]
    (reset! editor (make-editor config))))

;; Tutorial

(em/defaction render-level [level]
  [".intro"] (em/add-class "hide")
  [".editor"] (em/do->
                (em/remove-class "hide")
                (em/add-class "show"))
  [".result"] (em/do->
                (em/content "")
                (em/remove-class "success"))
  [".answer"] (em/content (pr-str (:goal level)))
  [".description"] (em/content (:description level)))

(defn current-level []
  (nth levels @current-level-index))

(defn show-current-level []
  (let [level (current-level)]
    (render-level level)
    (set-state (pr-str (:code level)))))

(em/defaction render-success []
  [".result"] (em/add-class "success"))

(em/defaction show-finish []
  [".editor"] (em/do->
                (em/remove-class "show")
                (em/add-class "hide"))
  [".finish"] (em/add-class "show"))

(defn next-level []
  (swap! current-level-index inc)
  (if (= (count levels) @current-level-index)
    (show-finish)
    (show-current-level)))

(defn back-level []
  (if (> @current-level-index 0)
    (do (swap! current-level-index dec)
        (show-current-level))))

(defn check-result [result]
  (let [goal (:goal (current-level))]
    (if (= result goal)
      (do (render-success)
          (js/setTimeout next-level 2000)))))

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

(em/defaction init-intro []
  [".back"] (em/listen
              :click
              back-level)
  [".intro a"] (em/listen
                 :click
                 show-current-level))

(set!
  (.-onload js/window)
  (fn []
    (init-editor)
    (init-intro)))

