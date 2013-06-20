
(ns core-logic-talk.data)

(def levels [

  {:name "Run Logic Run"
   :description "The first step in logic programming is knowing
                how to run a program.  (Hint: Focus the editor
                and hit CTRL-E)"
   :code '(run* [q])
   :goal '(_0)}

  {:name "Second level!"
   :description "Well done! The next thing to know about run is
                how to ask for more than one answer..."
   :code '(run* [q]
            (== 1 q))
   :goal '(1)}

])

