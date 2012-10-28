(ns metric-time
  (:require [goog.Timer :as timer]
            [goog.events :as events]))

(def fps 60)
(def background-color '(255 255 255))
(def foreground-color '(0 0 0))

(defn ^:export go [w h]
  (js/p.size w h)
  (js/p.background 255 255 255)
  (events/listen (doto (goog.Timer. (/ 1000 fps))
                   (. start))
                 goog.Timer/TICK draw))

;; Can certainly make the cleanup more efficient by only clearing dirty areas
(defn cleanup []
  (apply js/p.fill background-color)
  (apply js/p.stroke background-color)
  (js/p.rect 0 0 js/p.width js/p.height))

(defn clock-base []
  (js/p.pushMatrix)
  (js/p.translate (/ js/p.width 2) (/ js/p.height 2))
  (js/p.noFill)
  (apply js/p.stroke foreground-color)
  ; Outline
  (js/p.strokeWeight 2)
  (dotimes [i 2]
    (js/p.ellipse 0 0
                  (/ (* (+ i 1) js/p.width) 3)
                  (/ (* (+ i 1) js/p.height) 3)))
  (js/p.popMatrix))

(defn draw []
  (let [now (js/Date.)
        day-frac (/ (+ (* 60 (+ (* 60 (. now getHours))
                                (. now getMinutes)))
                       (. now getSeconds))
                    86400)
        period-frac (rem day-frac 0.01)]
    (cleanup)
    (clock-base)))
