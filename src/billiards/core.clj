(ns billiards.core
  (:use [clojure.tools.namespace.repl :only (refresh)]
    [billiards.gui.main :only [start-game redisplay]]
    [billiards.globals]
    [billiards.physics.ball_physics]
    [billiards.logic.main])
  (:gen-class))

(defn get-pockets []
  [[board-padding board-padding]
   [(/ board-width 2) board-padding]
   [(- board-width board-padding) board-padding]
   [board-padding (- board-height board-padding)]
   [(/ board-width 2) (- board-height board-padding)]
   [(- board-width board-padding) (- board-height board-padding)]])

(defn get-borders []
  [{:start [border-size border-size] :end [border-size (- board-height border-size)] :normal [1 0]}
   {:start [border-size (- board-height border-size)] :end [(- board-width border-size) (- board-height border-size)] :normal [0 1]}
   {:start [(- board-width border-size) border-size] :end [(- board-width border-size) (- board-height border-size)] :normal [-1 0]}
   {:start [border-size border-size] :end [(- board-width border-size) border-size] :normal [0 -1]}])

(defn create-ball [x y color]
  (ref {:x x :y y :color color :speed 0.0 :dirx 0.0 :diry 0.0 :friction-counter friction-counter-start}))

(defn create-triangle []
  (let [start-x (+ (* board-width 1/8) ball-size)
        start-y (+ (/ (- board-height (* 5 (* 2 ball-size))) 2) ball-size)]
    (loop [row 5 result []]
      (if (> row 0)
        (let [current-row (for [current (range row)]
                            (create-ball
                              (+ start-x (* (* 2 ball-size) (- 5 row)))
                              (+ start-y (+ (* (- 5 row) ball-size) (* (* 2 ball-size) current)))
                              :red))]
          (recur (- row 1) (into result current-row)))
        result))))

(defn get-initial-balls []
  (into (create-triangle) [(create-ball (* board-width 7/8) (* board-height 1/2) :white)]))

(defn start []
  (reset! balls (get-initial-balls))
  (reset! borders (get-borders))
  (reset! pockets (get-pockets))
  (start-game))

(defn restart []
  (refresh)
  (reset! is-playing true)
  (start))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (start))
