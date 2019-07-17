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

## 必要な環境変数

```
export VR_MATCH_FIREBASE_SERVICE_ACCOUNT_KEY="$(cat /path/to/credential.json)"

export FIREBASE_API_KEY="DUMMY_API_KEY"
export FIREBASE_AUTH_DOMAIN="DUMMY_AUTH_DOMAIN"
export FIREBASE_DATABASE_URL="DUMMY_DATABASE_URL"
export FIREBASE_PROJECT_ID="DUMMY_PROJECT_ID"
export FIREBASE_STORAGE_BUCKET="DUMMY_STORAGE_BUDKET"
export FIREBASE_MESSAGING_SENDER_ID="DUMMY_MESSAGING_SENDER_ID"
export FIREBASE_APP_ID="DUMMY_APP_ID"

export API_ENDPOINT="http://localhost:8080"
```

[direnv](https://github.com/direnv/direnv)などで適宜設定してください。

## 参考技術スタック

- Clojure, ClojureScript
- reagent
- re-frame
- React.js
- workbox
- re-ducks
- material-ui

ビルド方法などは `front-end` フォルダ及び `api` フォルダの `README.md` を見てください。
