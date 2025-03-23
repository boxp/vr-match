(ns cljsjs.material-ui
  (:require ["@material-ui/core" :as mui]))

;; グローバル変数の公開
(js/goog.exportSymbol "MaterialUI" mui)