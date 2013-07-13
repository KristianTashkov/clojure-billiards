(ns billiards.gui.main
  (:use
    [seesaw core color graphics behave]
    [billiards.state.gui :only [painting-future]]
    [billiards.gui.actions :only [mouse-moved mouse-released]]
    [billiards.gui.drawing :only [draw-table]]
    [billiards.constants :only [window-width window-height]]))

(defn redisplay [root]
  (config! (select root [:.billiard]) :paint draw-table))

(defn make-panel []
  (border-panel
    :center (canvas :paint draw-table
              :class :billiard
              :background "#013220"
              :cursor :crosshair)))

(defn make-frame []
  (frame :title   "Billiards"
    :size    [window-width :by window-height]
    :resizable? false
    :on-close :dispose
    :content (make-panel)))

(defn new-thread-run [action]
  (future (try
            (action)
            (catch Exception e (do
                                 (println e)
                                 (.printStackTrace e))))))

(defn add-bindings [frame]
  (listen frame :mouse-dragged (fn [e] (mouse-moved e)))
  (listen frame :mouse-moved (fn [e] (mouse-moved e)))
  (listen frame :mouse-released (fn [e] (new-thread-run #(mouse-released e)))))

(defn start-painting-thread [frame]
  (try
    (when @painting-future
      (future-cancel @painting-future))
    (catch Exception e))
  (reset! painting-future (new-thread-run (fn []
                                            (while frame
                                              (redisplay frame)
                                              (Thread/sleep 5))))))

(defn start-game []
  (let [frame (make-frame)]
    (add-bindings frame)
    (native!)
    (config! frame :content (make-panel))
    (show! frame)
    (start-painting-thread frame)
    "Have fun playing!"))
