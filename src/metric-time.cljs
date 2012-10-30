(ns metric-time
  (:require [goog.Timer :as timer]
            [goog.events :as events]))

(def fps 60)
(def background-color [255 255 255])
(def foreground-color [0 0 0])

(defn ^:export go [w h]
  (js/p.size w h)
  (js/p.background background-color)
  (events/listen (doto (goog.Timer. (/ 1000 fps))
                   (. start))
                 goog.Timer/TICK draw))

;;; Scale output (from 0 to 1) to size of canvas
(defn scaled [[x y]]
  [(* x js/p.width) (* y js/p.height)])

;;; Draw and scale a two-point processing function
(defn process [fn pa pb]
  (apply fn (concat (scaled pa) (scaled pb))))

;;; Convert a float between 0 and 1 to an angle in radians
(defn angle [frac]
  (let [pi 3.1415926535]
    (* frac pi 2)))

;; Can certainly make the cleanup more efficient by only clearing dirty areas
(defn cleanup []
  (apply js/p.fill background-color)
  (apply js/p.stroke background-color)
  (process js/p.rect [0 0] [1 1]))

(defn clock-base []
  (js/p.pushMatrix)
  (js/p.translate (/ js/p.width 2) (/ js/p.height 2))
  (js/p.noFill)
  (apply js/p.stroke foreground-color)

  ; Outline
  (js/p.strokeWeight 2)
  (dotimes [i 2] (process js/p.ellipse [0 0] (repeat 2 (/ (+ i 1) 3))))

  ; Notches (would look a bit cleaner if Ratio were supported in CLJS)
  (js/p.strokeWeight 1)
  (let [a (angle (/ 100))
        end (/ 11 30)]
    (js/p.rotate (/ 3.1415926535 2))
    (dotimes [i 100]
      (let [start (if (= 0 (mod i 10)) (/ 9 30) (/ 3))]
        (process js/p.line [start 0]  [end 0])
        (js/p.rotate a))))
  (js/p.popMatrix))

(defn clock-hands []
  (js/p.pushMatrix)
  (js/p.translate (/ js/p.width 2) (/ js/p.height 2))
  (js/p.rotate (/ -3.1415926535 2))
  (let [now (js/Date.)
        day-frac (/ (+ (* 60 (+ (* 60 (. now getHours))
                                (. now getMinutes)))
                       (. now getSeconds))
                    86400)
        period-frac (- (* 100 day-frac) (int (* 100 day-frac)))]
    (js/p.pushMatrix)
    (js/p.strokeWeight 2)
    (js/p.rotate (angle period-frac))
    (process js/p.line [0 0] [(/ 11 30) 0])
    (js/p.popMatrix)
    (js/p.strokeWeight 3)
    (js/p.rotate (angle day-frac))
    (process js/p.line [0 0] [(/ 11 30) 0]))
  (js/p.popMatrix))

(defn draw []
  (cleanup)
  (clock-base)
  (clock-hands))
