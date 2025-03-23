
// 簡素化したプリアンブルファイル
// shadow-cljsを使用することでほとんど不要になりますが、
// 移行期間中は一部の機能をサポートする場合があります

// Material-UIのSSRサポートのためのグローバル設定
global.window = global;
global.window.navigator = {
  userAgent: ""
};
global.window.localStorage = null;
