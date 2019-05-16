(ns vr-match.favorite.container
  (:require [reagent.core :as reagent]
            [vr-match.util :as util]
            [vr-match.favorite.component :refer [favorite-component]]
            [vr-match.events :as events]
            [re-frame.core :as re-frame]))

(def mock-favorite-state
  (reagent/atom {:items []}))

(defn handle-did-mount
  []
  (js/setTimeout
   (fn []
     (swap! mock-favorite-state
            #(-> %
                 (assoc :items
                        (->>
                         [{:id 1
                           :title "サンプル画像"
                           :userName "一箱"
                           :introduction "バーチャル清楚系女子高校生Webアプリケーションエンジニアおじさんです。こっそりプログラミングしてます。"
                           :platForms [{:id 1 :name "VRChat"} {:id 2 :name "VRoidHub"} {:id 3 :name "VirtualCast"}]
                           :image "https://storage.googleapis.com/boxp-tmp/profile_sample.png"}
                          {:id 2
                           :title "サンプル画像"
                           :userName "ヒマリ"
                           :introduction "一箱さんちのヒマリです！"
                           :platForms [{:id 1 :name "VRChat"} {:id 3 :name "VirtualCast"}]
                           :image "https://storage.googleapis.com/boxp-tmp/profile_sample_2.jpg"}
                          {:id 3
                           :title "サンプル画像"
                           :userName "アリシア・ソリッド"
                           :introduction "ニコニ立体で公式キャラクターやってます。よろしくお願いします！"
                           :platForms [{:id 3 :name "VirtualCast"}]
                           :image "https://storage.googleapis.com/boxp-tmp/profile_sample_3.jpg"}])))))
   300))

(defn handle-go-to-profile
  [id]
  (re-frame/dispatch [::events/push (str "/profile/" id)]))

(defn favorite
  [params]
  [favorite-component (merge {:handleDidMount handle-did-mount
                              :handleClickItem handle-go-to-profile}
                             @mock-favorite-state)])

(util/universal-set-loaded! :favorite)
