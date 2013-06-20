
(ns core-logic-talk.data)

(def levels [

  {:description "The first step in logic programming is knowing
                how to run a program.  (Hint: Focus the editor
                and hit CTRL-E)"
   :code '(run* [q])
   :goal '(_0)}

  {:description "Well done! The next thing to know about is unification,
                that's the double equals.  This tries to make our
                logic variable q equal to something."
   :code '(run* [q]
            (== :foo q))
   :goal '(:foo)}

  {:description "Ok your turn. Try and unify q with the expected goal.."
   :code '(run* [q]
            (== :foo q))
   :goal '(:bar)}

  {:description "Great!  So now we've got an idea of unification, but what if we
                want to make some new logic variables to use in addition to q.
                Meet fresh."
   :code '(run* [q]
                (fresh [x]
                       (== x 1)
                       (== :foo q)))
   :goal '(1)}

])

