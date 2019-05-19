
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

// SSRサーバーを :optimization :simple でビルドする時、モジュール解決に失敗して以下のエラーが出るので無理やり補完する
// TypeError: Cannot read property 'renderToString' of undefined
// goog.global = global;

// for https://github.com/firebase/firebase-js-sdk/issues/965
self = {
    fetch: function(){},
};
