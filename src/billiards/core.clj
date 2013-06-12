(ns billiards.core
  (:use [clojure.tools.namespace.repl :only (refresh set-refresh-dirs)]
    [billiards.gui.main :only [start-game redisplay]]
    [billiards.globals]
    [billiards.physics.ball_physics])
  (:gen-class))

(defn get-initial-borders []
  [{:start [0 0] :end [0 board-height]}
   {:start [0 board-height] :end [board-width board-height]}
   {:start [board-width 0] :end [board-width board-height]}
   {:start [0 0] :end [board-width 0]}])

(defn create-ball [x y dirx diry color]
  (ref {:x x :y y :color color :speed 20.0 :dirx dirx :diry diry}))

(defn get-initial-balls []
  [(create-ball 40.0 20.0 0.5 0.5 :white)
   (create-ball 240.0 40.0 -0.5 0.5 :black)])

(defn step []
  (dosync
    (doseq [ball @balls]
      (move-ball ball))
    (future (redisplay @main-frame))))

(defn collisions []
  (dosync
    (doseq [ball @balls]
      (collision-border-ball ball))))

(defn reset-speed []
  (dosync
    (doseq [ball @balls]
      (alter ball update-in [:speed] (fn [x] 20.0)))))

(defn turn []
  (while (not-every? #(= 0 (:speed @%)) @balls)
    (step)
    (collisions)
    (Thread/sleep 15))
  (reset-speed))

(defn start []
  (reset! balls (get-initial-balls))
  (reset! borders (get-initial-borders))
  (start-game))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false)))
