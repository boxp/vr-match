(ns vr-match.approach.component
  (:require [cljs.spec.alpha :as s]
            [reagent.core :as r]
            [vr-match.lib.components.material-ui :as mui]
            [vr-match.lib.component :refer [navigation-bar-layout]]
            [vr-match.lib.components.linear-progress :refer [linear-progress]]
            [vr-match.approach.components.swipe-card-item :refer [swipe-card-item]]
            [vr-match.approach.components.action-buttons :refer [action-buttons]]
            [vr-match.approach.components.favorite-overlay :refer [favorite-overlay]]
            [vr-match.approach.components.skip-overlay :refer [skip-overlay]]
            [vr-match.approach.components.matching-dialog :refer [matching-dialog]]
            [vr-match.approach.components.empty :refer [swipe-cards-empty]]))

(def swipe-return-limit 150)

(def approach-state
  (r/atom {:firstCard nil
           :secondCard nil
           :isDragging false
           :isSkip false
           :isFavorite false
           :isReturning false
           :swipeStartPosition {:x 0
                                :y 0}
           :swipeCurrentPosition {:x 0
                                  :y 0}}))

(def card-ref (r/atom nil))

(defn- onClickSkip
  [props]
  (swap! approach-state
         #(-> % (assoc :isSkip true))))

(defn- onClickFavorite
  [props]
  (swap! approach-state
         #(-> %
              (assoc :isFavorite true))))

(defn- onSwipeCardTouchStart
  [event]
  (let [position-x (or (some-> event .-targetTouches (aget 0) .-pageX)
                       (some-> event .-pageX))
        position-y (or (some-> event .-targetTouches (aget 0) .-pageY)
                       (some-> event .-pageY))]
    (-> approach-state
        (swap! #(-> %
                    (assoc :isDragging true)
                    (assoc :isReturning false)
                    (assoc :isSkip false)
                    (assoc :isFavorite false)
                    (assoc-in [:swipeStartPosition :x] position-x)
                    (assoc-in [:swipeStartPosition :y] position-y)
                    (assoc-in [:swipeCurrentPosition :x] position-x)
                    (assoc-in [:swipeCurrentPosition :y] position-y))))))

(defn- onSwipeCardTouchMoved
  [event]
  (when (-> @approach-state :isDragging)
    (let [position-x (or (some-> event .-targetTouches (aget 0) .-pageX)
                         (some-> event .-pageX))
          position-y (or (some-> event .-targetTouches (aget 0) .-pageY)
                         (some-> event .-pageY))]
      (-> approach-state
          (swap!
           #(-> %
                (assoc-in [:swipeCurrentPosition :x] position-x)
                (assoc-in [:swipeCurrentPosition :y] position-y))))
      (.. event preventDefault))))

(defn- onSwipeCardTouchEnd
  [event]
  (let [add-x (- (-> @approach-state :swipeCurrentPosition :x)
                 (-> @approach-state :swipeStartPosition :x))]
    (cond (< add-x (- swipe-return-limit))
          (-> approach-state
              (swap! #(-> % (assoc :isSkip true)
                          (assoc :isDragging false))))
          (> add-x swipe-return-limit)
          (-> approach-state
              (swap! #(-> % (assoc :isFavorite true)
                          (assoc :isDragging false))))
          :else
          (-> approach-state
              (swap! #(-> % (assoc :isReturning true)
                          (assoc :isDragging false)))))))

(defn- handleOnExit
  [{:keys [cardItems
           hasNextPage
           handleFetchNext
           handleClickFavorite
           handleClickSkip] :as props}]
  (let [should-fetch-next? (and (<= (-> cardItems count) 6)
                                hasNextPage)
        current-card (-> @approach-state :firstItem)
        shift-card-items (fn [state]
                           (-> state
                               (assoc :firstItem (-> cardItems second))
                               (assoc :secondItem (-> cardItems (nth 3 nil)))))]
    (cond (:isSkip @approach-state)
          (do (handleClickSkip (:id current-card))
              (swap! approach-state shift-card-items)
              (when should-fetch-next? (handleFetchNext)))
          (:isFavorite @approach-state)
          (do (handleClickFavorite current-card)
              (swap! approach-state shift-card-items)
              (when should-fetch-next? (handleFetchNext))))
    (swap! approach-state
           #(-> %
                (assoc :isSkip false)
                (assoc :isFavorite false)
                (assoc :isReturning false)
                (assoc :swipeStartPosition {:x 0 :y 0})
                (assoc :swipeCurrentPosition {:x 0 :y 0})))))

(defn- handleClickGoToProfile
  [props id]
  ((:handleClickGoToProfile props) id))

(defn- state->current-swipe-card-transform
  [state]
  (let [add-x (- (-> state :swipeCurrentPosition :x)
                 (-> state :swipeStartPosition :x))
        add-y (- (-> state :swipeCurrentPosition :y)
                 (-> state :swipeStartPosition :y))
        rotate (/ add-x 26)]
    (str "translate("
         add-x "px,"
         add-y "px) "
         "rotate("
         rotate "deg)")))

(defn- state->return-swipe-card-animation
  [state]
  (str
   "@keyframes returnSwipeCard {
     from {
       transform: " (state->current-swipe-card-transform state) "
     }
     to {
       transform: translate(0, 0) rotate(0);
     }
   }
   .return-animation {
     animation-name: returnSwipeCard;
     animation-duration: 500ms;
     animation-timing-function: cubic-bezier(0.460, 0.185, 0.285, 1.195);
     animation-iteration-count: 1;
     animation-fill-mode: both;
   }"))

(defn- state->favorite-swipe-card-animation
  [state]
  (let [add-x (- (-> state :swipeCurrentPosition :x)
                 (-> state :swipeStartPosition :x))
        add-y (- (-> state :swipeCurrentPosition :y)
                 (-> state :swipeStartPosition :y))
        rotate (+ (/ add-x 26) 30)]
    (str
     "@keyframes favoriteSwipeCard {
     from {
       transform: " (state->current-swipe-card-transform state) "
     }
     to {
       transform: translate(150%, " add-y "px) rotate(" rotate "deg);
     }
   }
   .favorite-animation {
     animation-name: favoriteSwipeCard;
     animation-duration: 300ms;
     animation-timing-function: linear;
     animation-iteration-count: 1;
     animation-fill-mode: both;
   }")))

(defn- state->skip-swipe-card-animation
  [state]
  (let [add-x (- (-> state :swipeCurrentPosition :x)
                 (-> state :swipeStartPosition :x))
        add-y (- (-> state :swipeCurrentPosition :y)
                 (-> state :swipeStartPosition :y))
        rotate (- (/ add-x 26) 30)]
    (str
     "@keyframes skipSwipeCard {
     from {
       transform: " (state->current-swipe-card-transform state) "
     }
     to {
       transform: translate(-150%, " add-y "px) rotate(" rotate "deg);
     }
   }
   .skip-animation {
     animation-name: skipSwipeCard;
     animation-duration: 300ms;
     animation-timing-function: linear;
     animation-iteration-count: 1;
     animation-fill-mode: both;
   }")))

(defn- state->favorite-overlay-opacity
  [state]
  (let [add-x (- (-> state :swipeCurrentPosition :x)
                 (-> state :swipeStartPosition :x))]
    (/ add-x swipe-return-limit)))

(defn- state->skip-overlay-opacity
  [state]
  (let [add-x (- (-> state :swipeCurrentPosition :x)
                 (-> state :swipeStartPosition :x))]
    (/ (- add-x) swipe-return-limit)))

(defn- empty-cards?
  [isLoaded cardItems hasNextPage]
  (and isLoaded
       (empty? cardItems)
       (not hasNextPage)))

(defn- register-card-event-handlers
  [card-ref this]
  (some-> card-ref
          (.addEventListener "touchstart" onSwipeCardTouchStart))
  (some-> card-ref
          (.addEventListener "touchmove"
                             onSwipeCardTouchMoved
                             #js {"passive" false}))
  (some-> card-ref
          (.addEventListener "touchend" onSwipeCardTouchEnd))
  (some-> card-ref
          (.addEventListener "animationend" #(-> this r/props handleOnExit)))
  (some-> card-ref
          (.addEventListener "mousedown" onSwipeCardTouchStart))
  (some-> card-ref
          (.addEventListener "mousemove"
                             onSwipeCardTouchMoved
                             #js {"passive" false}))
  (some-> card-ref
          (.addEventListener "mouseup" onSwipeCardTouchEnd)))

(defn- component-did-mount
  [this]
  (let [props (r/props this)]
    (swap! approach-state
           #(-> %
                (assoc :firstItem (-> props :cardItems first))
                (assoc :secondItem (-> props :cardItems second))))
    (register-card-event-handlers @card-ref this)
    (when (= (-> props :cardItems count) 0)
      ((:handleDidMount props)))))

(defn- component-did-update
  [this [_ old-props]]
  (let [new-props (r/props this)
        new-empty-cards? (empty-cards? (:isLoaded new-props)
                                       (:cardItems new-props)
                                       (:hasNextPage new-props))
        old-empty-cards? (empty-cards? (:isLoaded old-props)
                                       (:cardItems old-props)
                                       (:hasNextPage old-props))]
    (when (and (not= (some-> new-props :cardItems first :id)
                     (some-> old-props :cardItems first :id))
               (not (-> @approach-state :isSkip))
               (not (-> @approach-state :isFavorite))
               (not (-> @approach-state :isReturning))
               (not (-> @approach-state :isDragging)))
      (swap! approach-state
             #(-> %
                  (assoc :firstItem (-> new-props :cardItems first))
                  (assoc :secondItem (-> new-props :cardItems second)))))
    (when (and (not new-empty-cards?)
               old-empty-cards?)
      (register-card-event-handlers @card-ref this))))

(defn- component-will-unmount
  [this]
  (some-> @card-ref
          (.removeEventListener "touchstart" onSwipeCardTouchStart))
  (some-> @card-ref
          (.removeEventListener "touchmove" onSwipeCardTouchMoved))
  (some-> @card-ref
          (.removeEventListener "touchend" onSwipeCardTouchEnd))
  (some-> @card-ref
          (.removeEventListener "mousedown" onSwipeCardTouchStart))
  (some-> @card-ref
          (.removeEventListener "mousemove" onSwipeCardTouchMoved))
  (some-> @card-ref
          (.removeEventListener "mouseup" onSwipeCardTouchEnd)))

(def approach
  (r/create-class
    {:display-name "approach-component"
     :reagent-render
     (fn [{:keys [me
                  classes
                  cardItems
                  isLoaded
                  isLoading
                  isShowMatchingDialog
                  hasNextPage
                  matchingPartner
                  handleClickSkip
                  handleClickFavorite
                  handleClickMatchingDialogBack
                  handleFetchNext
                  handleResetAllSkip] :as props}]
       [navigation-bar-layout {:title "アバターをさがす"}
        [:<>
         (if (empty-cards? isLoaded cardItems hasNextPage)
           [swipe-cards-empty {:handleResetAllSkip handleResetAllSkip}]
           [:div {:style {:height "100%"
                          :display "flex"
                          :flex-direction "column"
                          :align-items "center"
                          :justify-content "space-around"}}
            [:div {:style {:display "flex"
                           :justify-content "center"
                           :flex-direction "column"
                           :position "relative"
                           :width "86vw"
                           :max-width "640px"
                           :height "64vh"
                           :max-height "960px"}}
             [:style (cond (:isReturning @approach-state)
                           (state->return-swipe-card-animation @approach-state)
                           (:isFavorite @approach-state)
                           (state->favorite-swipe-card-animation @approach-state)
                           (:isSkip @approach-state)
                           (state->skip-swipe-card-animation @approach-state)
                           :else nil)]
             [:div {:style {:z-index "1000"
                            :position "absolute"
                            :top 0
                            :left 0
                            :bottom 0
                            :right 0
                            :will-change "transform"}}
              [swipe-card-item {:item (-> @approach-state :secondItem)
                                :handleClickCard #()}]]
             [:div {:style {:will-change "transform"
                            :transform (when (:isDragging @approach-state)
                                         (state->current-swipe-card-transform @approach-state))
                            :animation-play-state (when
                                                    (or (:isReturning @approach-state)
                                                        (:isFavorite @approach-state)
                                                        (:isSkip @approach-state))
                                                    "running")
                            :position "absolute"
                            :top 0
                            :left 0
                            :bottom 0
                            :right 0
                            :z-index "1200"}
                    :class (cond (:isReturning @approach-state)
                                 "return-animation"
                                 (:isFavorite @approach-state)
                                 "favorite-animation"
                                 (:isSkip @approach-state)
                                 "skip-animation"
                                 :else nil)
                    :ref (fn [ref] (reset! card-ref ref))}
              [swipe-card-item {:item (-> @approach-state :firstItem)
                                :handleClickCard #(handleClickGoToProfile props %)}]
              [:div {:style {:will-change "opacity"
                             :position "absolute"
                             :height "100%"
                             :width "100%"
                             :top 0
                             :opacity (state->favorite-overlay-opacity @approach-state)
                             :pointer-events "none"}}
               [favorite-overlay]]
              [:div {:style {:will-change "opacity"
                             :position "absolute"
                             :height "100%"
                             :width "100%"
                             :top 0
                             :opacity (state->skip-overlay-opacity @approach-state)
                             :pointer-events "none"}}
               [skip-overlay]]]]
            [:div {:style {:will-change "transform"
                           :width "100%"
                           :max-width "360px"}}
             [action-buttons {:onClickSkip #(onClickSkip props)
                              :onClickFavorite #(onClickFavorite props)}]]
            [matching-dialog {:isOpen isShowMatchingDialog
                              :me me
                              :partner matchingPartner
                              :handleClickGoToProfile #(handleClickGoToProfile props %)
                              :handleClickBack handleClickMatchingDialogBack}]])
         (when isLoading
           [linear-progress])]])
     :component-did-mount component-did-mount
     :component-did-update component-did-update
     :component-will-unmount component-will-unmount}))
