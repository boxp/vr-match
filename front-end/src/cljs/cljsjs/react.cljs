(ns cljsjs.react
  (:require ["react" :as react]))

;; グローバル変数の公開（既存のコードとの互換性を保つため）
(js/goog.exportSymbol "React" react)