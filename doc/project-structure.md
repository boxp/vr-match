# VR-Match プロジェクト構造

VR-Matchは、フロントエンドとバックエンドの2つの主要なコンポーネントで構成されるマイクロサービスアーキテクチャを採用しています。

## プロジェクト概要

```
vr-match/
├── back-end/     # バックエンドサービス (Clojure)
├── front-end/    # フロントエンドアプリケーション (ClojureScript)
├── .github/      # GitHub Actions ワークフロー設定
└── doc/          # プロジェクトドキュメント
```

## バックエンド (back-end/)

クリーンアーキテクチャを採用したClojureベースのバックエンドサービス。

```
back-end/
├── src/vr_match_back_end/
│   ├── app/              # アプリケーション層
│   │   └── my_webapp/    # Webアプリケーションの設定とハンドラー
│   ├── domain/          # ドメイン層
│   │   ├── entity/     # エンティティ定義
│   │   └── usecase/    # ユースケース実装
│   └── infra/          # インフラストラクチャ層
│       ├── datasource/ # データソース実装
│       └── repository/ # リポジトリ実装
├── resources/
│   ├── migrations/     # データベースマイグレーション
│   └── graphql-schema.edn  # GraphQLスキーマ定義
└── k8s/               # Kubernetes設定ファイル
```

### 主要なコンポーネント
- GraphQLベースのAPI
- MySQL データベース
- Firebase認証
- Cloud Storage統合

## フロントエンド (front-end/)

ClojureScriptとre-frameを使用したSPAアプリケーション。

```
front-end/
├── src/
│   ├── cljs/           # 共有コード
│   │   └── vr_match/
│   │       ├── auth/   # 認証関連
│   │       ├── lib/    # 共通コンポーネント
│   │       ├── mypage/ # マイページ機能
│   │       └── wizard/ # ユーザー設定ウィザード
│   ├── cljs-client/    # クライアントサイド固有のコード
│   ├── cljs-server/    # サーバーサイド固有のコード
│   └── cljs-worker/    # Webワーカー関連のコード
├── resources/
│   └── public/         # 静的アセット
└── k8s/                # Kubernetes設定ファイル
```

### 主要な機能
- マテリアルUIベースのデザイン
- レスポンシブレイアウト
- プログレッシブウェブアプリ（PWA）対応
- サーバーサイドレンダリング（SSR）

## デプロイメント

プロジェクトはKubernetesを使用してデプロイされ、以下のファイルで設定が管理されています：

- `docker-compose.yaml` - ローカル開発環境
- `cloudbuild.yaml` - Cloud Build設定
- `back-end/k8s/` - バックエンドのKubernetes設定
- `front-end/k8s/` - フロントエンドのKubernetes設定

## CI/CD

GitHub Actionsを使用して以下のワークフローを自動化：

- フロントエンド/バックエンドのコンテナイメージビルド
- ステージング環境への自動デプロイ
- 本番環境へのデプロイ
- 自動マージ処理

これらの設定は `.github/workflows/` ディレクトリで管理されています。