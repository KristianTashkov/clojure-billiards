(ns billiards.core
  (:use [clojure.tools.namespace.repl :only (refresh)]
    [billiards.gui.main :only [start-game board-height board-width]])
  (:gen-class))

(defn get-initial-borders []
  [{:start [0 0] :end [0 board-height]}
   {:start [0 board-height] :end [board-width board-height]}
   {:start [board-width 0] :end [board-width board-height]}
   {:start [0 0] :end [board-width 0]}])

(defn get-initial-balls []
  [{:position [40 20] :type :white :speed 0}
   {:position [40 20] :type :black :speed 0}])

(defn get-initial-state []
  {:balls (get-initial-balls)
   :borders (get-initial-borders)})

(defn start []
  (let [state (ref (get-initial-state))]
    state))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (start))
