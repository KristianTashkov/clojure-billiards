(ns billiards.core
  (:use
    [clojure.tools.namespace.repl :only [refresh]]
    [billiards.state.initial :only [reset-game]]
    [billiards.gui.main :only [start-game]])
  (:gen-class))

(defn start []
  (reset-game)
  (start-game))

(defn -main
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (start))
