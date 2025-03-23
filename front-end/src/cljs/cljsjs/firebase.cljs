(ns cljsjs.firebase
  (:require ["firebase/app" :as firebase]))

;; グローバル変数の公開
(js/goog.exportSymbol "Firebase" firebase)

;; 追加サービスのインポート（必要に応じて）
(defn import-auth []
  (js/require "firebase/auth"))

(defn import-firestore []
  (js/require "firebase/firestore"))