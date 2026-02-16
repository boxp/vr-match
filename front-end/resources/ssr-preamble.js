
// https://github.com/reagent-project/reagent/issues/296
var React = require("react");
var ReactDOM = {server: require("react-dom/server")};
var ReactDOMServer = require("react-dom/server");
var createReactClass = require("create-react-class");
global.React = React;
global.ReactDOM = ReactDOM;
global.ReactDOMServer = ReactDOMServer;
global.createReactClass = createReactClass;

// material-ui の universal 対応のための workaround
// もっといい手があれば知りたい
global.window = global;
global.window.navigator = {};
global.window.navigator.userAgent = "";
global.window.localStorage = null;

// SSRサーバーを :optimization :simple でビルドする時、モジュール解決に失敗して以下のエラーが出るので無理やり補完する
// TypeError: Cannot read property 'renderToString' of undefined
// goog.global = global;

// for https://github.com/firebase/firebase-js-sdk/issues/965
self = {
    fetch: function(){},
};

// Firebase v8 をロードしてグローバルに設定
var firebase = require("firebase/app");
require("firebase/auth");
global.firebase = firebase;

// cljsjs/firebase v5 互換シム
// cljsjs/firebase 5.7.3 のインラインUMDコード (firebase-auth.inc.js) は
// firebase.INTERNAL.registerService() を呼んでサービスを登録しようとする。
// firebase v8 では registerService が registerComponent に置き換わっているため、
// そのままでは "Cannot find the firebase namespace" エラーが発生する。
// 以下のシムは registerService を追加し、既にv8で登録済みのサービスへの
// 重複登録を安全にスキップする。
if (firebase.INTERNAL && !firebase.INTERNAL.registerService) {
    firebase.INTERNAL.registerService = function(name) {
        return firebase[name] || null;
    };
}
