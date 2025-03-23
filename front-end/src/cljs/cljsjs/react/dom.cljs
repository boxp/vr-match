(ns cljsjs.react.dom
  (:require ["react-dom" :as react-dom]
            ["react-dom/server" :as react-dom-server]))

;; グローバル変数の公開
(js/goog.exportSymbol "ReactDOM" react-dom)
(js/goog.exportSymbol "ReactDOMServer" react-dom-server)