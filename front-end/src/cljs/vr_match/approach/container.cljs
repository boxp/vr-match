(ns vr-match.approach.container
  (:require [reagent.core :as reagent]
            [vr-match.approach.component :as component]
            [vr-match.approach.events :as approach-events]
            [vr-match.approach.subs :as approach-subs]
            [vr-match.util :as util]
            [vr-match.events :as events]
            [re-frame.core :as re-frame]))

(def user-per-page 12)

(declare mock-approach-state)

(defn handle-click-skip
  [id]
  (re-frame/dispatch [::approach-events/skip id]))

(defn handle-click-favorite
  [id]
  (re-frame/dispatch [::approach-events/favorite id]))

(defn handle-fetch-next []
  (re-frame/dispatch [::approach-events/fetch-next-approach-list {:count user-per-page}]))

(defn handle-did-mount
  []
  (re-frame/dispatch [::approach-events/fetch-approach-list {:count user-per-page}])
  (js/setTimeout
   (fn []
     (swap! mock-approach-state
            #(-> %
                 (assoc :me {:id 1
                             :title "サンプル画像"
                             :userName "一箱"
                             :introduction "バーチャル清楚系女子高校生Webアプリケーションエンジニアおじさんです。こっそりプログラミングしてます。"
                             :platforms [{:id 1 :name "VRChat"} {:id 2 :name "VRoidHub"} {:id 3 :name "VirtualCast"}]
                             :image "https://storage.googleapis.com/boxp-tmp/profile_sample.png"}))))
   300))

(defn handle-click-go-to-profile
  [id]
  (re-frame/dispatch [::events/push (str "/profile/" id)]))

;; TODO: re-frameとつなぎこんで消す
(def mock-approach-state
  (reagent/atom {:cardItems []}))

(defn approach
  [params]
  (let [card-items (re-frame/subscribe
                    [::approach-subs/approach-list])]
    [component/approach (merge @mock-approach-state {:handleClickSkip handle-click-skip
                                                     :handleClickFavorite handle-click-favorite
                                                     :handleClickGoToProfile handle-click-go-to-profile
                                                     :handleDidMount handle-did-mount
                                                     :handleFetchNext handle-fetch-next
                                                     :cardItems @card-items})]))

(util/universal-set-loaded! :approach)
