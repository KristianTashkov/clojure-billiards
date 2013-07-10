(ns billiards.core
  (:use
    [clojure.tools.namespace.repl :only (refresh)]
    [billiards.gui.main :only [start-game]]
    [billiards.globals]
    [billiards.physics.ball_physics]
    [billiards.physics.geometry]
    [billiards.logic.main])
  (:gen-class))

(defn get-long-side-border-points [startx starty direction is-left]
  (let [left-x (* (if is-left corner-pocket-angle-percent middle-pocket-angle-percent) border-size)
        right-x (* (if is-left middle-pocket-angle-percent corner-pocket-angle-percent) border-size)
        [ax ay] [startx starty]
        [dx dy] [(+ startx long-side-length) starty]
        [bx by] [(+ ax left-x) (+ ay (* direction border-size))]
        [cx cy] [(- dx right-x) by]]
    [[ax ay] [bx by] [cx cy] [dx dy]]))

(defn get-short-side-border-points [startx starty direction]
  (let [diry (* corner-pocket-angle-percent border-size)
        [ax ay] [startx starty]
        [dx dy] [startx (+ starty short-side-length)]
        [bx by] [(+ ax (* direction border-size)) (+ ay diry)]
        [cx cy] [bx (- dy diry)]]
    [[ax ay] [bx by] [cx cy] [dx dy]]))

(defn get-border-points []
  (let [result (into []     (get-long-side-border-points pocket-size 0 1 true))
        result (into result (get-long-side-border-points (+ (* 2 pocket-size ) long-side-length) 0 1 false))
        result (into result (get-long-side-border-points pocket-size board-height -1 true))
        result (into result (get-long-side-border-points (+ (* 2 pocket-size ) long-side-length) board-height -1 false))
        result (into result (get-short-side-border-points 0 pocket-size 1))
        result (into result (get-short-side-border-points board-width pocket-size -1))]
    result))

(defn get-pockets []
  [[board-padding board-padding]
   [(/ board-width 2) (- board-padding (* pocket-size 2/3))]
   [(- board-width board-padding) board-padding]
   [board-padding (- board-height board-padding)]
   [(/ board-width 2) (+ (- board-height board-padding) (* pocket-size 2/3))]
   [(- board-width board-padding) (- board-height board-padding)]])

(defn create-border [start end]
  (swap! borders conj {:start start
                       :end end
                       :normal (normalize-vector (get-perp-of-vector (sub-vect start end)))}))

(defn generate-borders []
  (loop [[a b c d] (take 4 @border-points) other (take-last (- (count @border-points) 4) @border-points)]
    (when (and a b c d)
      (create-border a b)
      (create-border b c)
      (create-border c d)
      (recur (take 4 other) (take-last (- (count other) 4) other)))))

(defn get-ball-color [row number]
  (let [color-map {[1 0] :red
                   [2 0] :yellow
                   [3 0] :red
                   [4 0] :yellow
                   [5 0] :red
                   [5 1] :yellow
                   [5 2] :red
                   [5 3] :yellow
                   [5 4] :red
                   [4 3] :yellow
                   [3 2] :red
                   [2 1] :yellow
                   [3 1] :black
                   [4 1] :yellow
                   [4 2] :red}]
    (color-map [row number])))

(defn create-triangle []
  (let [start-x (+ (* board-width 1/8) ball-size)
        start-y (+ (/ (- board-height (* 5 (* 2 ball-size))) 2) ball-size)]
    (loop [row 5 result []]
      (if (> row 0)
        (let [current-row (for [current (range row)]
                            (create-ball
                              (+ start-x (* (* 2 ball-size) (- 5 row)))
                              (+ start-y (+ (* (- 5 row) ball-size) (* (* 2 ball-size) current)))
                              (get-ball-color row current)))]
          (recur (- row 1) (into result current-row)))
        result))))

(defn get-initial-balls []
  (into (create-triangle) [(create-ball (* board-width 7/8) (* board-height 1/2) :white)]))

(defn start []
  (dosync
    (ref-set balls (get-initial-balls)))
  (reset! border-points (get-border-points))
  (generate-borders)
  (reset! pockets (get-pockets))
  (start-game))

(defn -main
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (start))
