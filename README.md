# Hito Hub

アバター×アバターのマッチングアプリ予定地です

## ディレクトリ構造(TBD)

```
front-end
├── k8s: Kubernetes用マニフェストファイル置き場
├── resources
│   └── public: SSR用サーバーから返す静的ファイル置き場(静的画像とビルド後のjsバンドルのみ)
└── src
    ├── clj: マクロ置き場
    ├── cljs
    │   └── vr_match
    │       ├── lib: 全画面共通の実装を置く場所
    │       └── その他のフォルダ: wizard,loginなど機能別の実装を置く場所(re-ducksを参照)
    ├── cljs-client: クライアントサイドのエントリーポイント
    └── cljs-server: SSR用サーバーのエントリーポイント
    └── cljs-worker: WebWorkerのエントリーポイント
```

## 参考技術スタック

- Clojure, ClojureScript
- reagent
- re-frame
- React.js
- workbox
- re-ducks
- material-ui

ビルド方法などは `front-end` フォルダ及び `api` フォルダの `README.md` を見てください。
