(ns billiards.core
  (:use [clojure.tools.namespace.repl :only (refresh set-refresh-dirs)]
    [billiards.gui.main :only [start-game redisplay]]
    [billiards.globals]
    [billiards.physics.ball_physics]
    [billiards.logic.main])
  (:gen-class))

(defn get-initial-borders []
  [{:start [0 0] :end [0 board-height]}
   {:start [0 board-height] :end [board-width board-height]}
   {:start [board-width 0] :end [board-width board-height]}
   {:start [0 0] :end [board-width 0]}])

(defn create-ball [x y dirx diry color]
  (ref {:x x :y y :color color :speed 0.0 :dirx 0.0 :diry 0.0}))

(defn get-initial-balls []
  [(create-ball 40.0 20.0 0.5 0.5 :white)
   (create-ball 240.0 40.0 -0.5 0.5 :black)])

(defn start []
  (reset! balls (get-initial-balls))
  (reset! borders (get-initial-borders))
  (start-game))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (start))
