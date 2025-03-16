# Hito Hub プロジェクト仕様書

## 1. プロジェクト概要

Hito Hubは、VRアバターのためのマッチングサービスです。ユーザーはVRプラットフォーム上で使用するアバターの情報を登録し、他のユーザーとマッチングすることができます。本サービスは、VR空間での出会いを促進し、共通の趣味や興味を持つユーザー同士の交流を支援します。

## 2. システムアーキテクチャ

### 2.1 全体構成

Hito Hubは、マイクロサービスアーキテクチャを採用しており、以下の主要コンポーネントで構成されています：

- **フロントエンド**: ClojureScript + re-frame + React.js
- **バックエンド**: Clojure（クリーンアーキテクチャ）
- **データベース**: MySQL
- **認証**: Firebase Authentication
- **ストレージ**: Cloud Storage
- **API**: GraphQL

### 2.2 技術スタック詳細

#### 2.2.1 フロントエンド
- ClojureScript: JavaScriptにコンパイルされるClojureの方言
- reagent: React.jsのClojureScriptラッパー
- re-frame: Flux/Reduxライクな状態管理ライブラリ
- material-ui: マテリアルデザインのUIコンポーネント
- workbox: PWA（Progressive Web App）対応ライブラリ

#### 2.2.2 バックエンド
- Clojure: JVM上で動作する関数型プログラミング言語
- クリーンアーキテクチャ: ドメイン駆動設計に基づく設計手法
- GraphQL: 柔軟なAPIクエリ言語
- MySQL: リレーショナルデータベース
- Firebase Authentication: ユーザー認証サービス
- Cloud Storage: ファイルストレージサービス

#### 2.2.3 インフラストラクチャ
- Kubernetes: コンテナオーケストレーション
- Docker: コンテナ化技術
- GitHub Actions: CI/CD（継続的インテグレーション/デリバリー）

## 3. 機能仕様

### 3.1 ユーザー管理

#### 3.1.1 ユーザー登録・認証
- Firebase Authenticationを使用した認証システム
- 以下の認証方法をサポート：
  - メールアドレス登録
  - Twitterログイン
- セッション管理（トークンベース）

#### 3.1.2 ユーザープロフィール
- 基本情報
  - 名前
  - 自己紹介文
- プロフィール画像
  - メイン画像（必須）
  - 複数のサブ画像（任意）
- プラットフォーム情報
  - 複数のVRプラットフォームのユーザーIDを登録可能
  - プラットフォームごとのプロフィールリンク

### 3.2 マッチング機能

#### 3.2.1 アプローチ機能
- 他のユーザーを閲覧
- お気に入り登録（いいね）機能
- スキップ（興味なし）機能
- 相互いいねでマッチング成立

#### 3.2.2 マッチング管理
- マッチングしたユーザー一覧表示
- お気に入りに登録したユーザー一覧表示
- 自分をお気に入りに登録したユーザー一覧表示
- スキップしたユーザーを再表示する機能（リセット機能）

### 3.3 画面構成

#### 3.3.1 認証関連
- ウェルカム画面
- 登録画面
- メール登録画面
- メール登録完了画面
- Twitterログイン画面
- メールログイン画面
- メールログイン完了画面

#### 3.3.2 プロフィール設定
- ウィザード画面（初回登録時）
- マイプロフィール編集画面
- 設定画面
- サードパーティ認証設定画面

#### 3.3.3 マッチング関連
- アプローチ画面（他ユーザー閲覧）
- お気に入りユーザー一覧画面
- お気に入りされたユーザー一覧画面
- マッチング成立ユーザー一覧画面
- ユーザープロフィール詳細画面
- マイページ

## 4. データモデル

### 4.1 ユーザー (user)
- id: ユーザーID（主キー）
- firebase_id: Firebase認証ID（一意）
- name: ユーザー名
- introduction: 自己紹介
- created_at: 作成日時
- updated_at: 更新日時

### 4.2 プラットフォーム (platform)
- id: プラットフォームID（主キー）
- name: プラットフォーム名
- url_template: URLテンプレート
- example_user_id: サンプルユーザーID
- created_at: 作成日時
- updated_at: 更新日時

### 4.3 ユーザープラットフォーム (user_platform)
- user_id: ユーザーID（外部キー）
- platform_id: プラットフォームID（外部キー）
- platform_user_id: プラットフォーム上のユーザーID
- created_at: 作成日時
- updated_at: 更新日時
- 主キー: (user_id, platform_id)

### 4.4 画像 (image)
- id: 画像ID（主キー）
- url: 画像URL
- placeholder_color: プレースホルダーカラー
- created_at: 作成日時
- updated_at: 更新日時

### 4.5 ユーザー画像 (user_image)
- user_id: ユーザーID（外部キー）
- image_id: 画像ID（外部キー）
- type: 画像タイプ（メイン/サブ）
- created_at: 作成日時
- updated_at: 更新日時

### 4.6 ユーザーお気に入り (user_favorite)
- user_id: お気に入りしたユーザーID（外部キー）
- favorite_user_id: お気に入りされたユーザーID（外部キー）
- created_at: 作成日時
- updated_at: 更新日時

### 4.7 ユーザーマッチ (user_match)
- user_id: ユーザーID（外部キー）
- match_user_id: マッチしたユーザーID（外部キー）
- created_at: 作成日時
- updated_at: 更新日時

### 4.8 ユーザースキップ (user_skip)
- user_id: スキップしたユーザーID（外部キー）
- skip_user_id: スキップされたユーザーID（外部キー）
- created_at: 作成日時
- updated_at: 更新日時

## 5. API仕様

Hito HubはGraphQLベースのAPIを提供しています。

### 5.1 主要なクエリ
- **approachList**: アプローチ可能なユーザーリスト取得
  - ページネーション対応
  - 除外ユーザー指定可能
- **favoritedUsers**: お気に入りユーザーリスト取得
  - ページネーション対応
- **matchedUsers**: マッチングしたユーザーリスト取得
  - ページネーション対応
- **favoritedFromUsers**: 自分をお気に入りに登録したユーザーリスト取得
  - ページネーション対応
- **me**: 自分のプロフィール取得
- **partner**: 特定ユーザーのプロフィール取得
- **platformOptions**: 利用可能なプラットフォーム一覧取得

### 5.2 主要なミューテーション
- **registerUser**: ユーザー登録
- **loginUser**: ログイン
- **uploadImage**: 画像アップロード
- **updateMe**: プロフィール更新
- **skip**: ユーザースキップ
- **favorite**: ユーザーをお気に入りに登録
- **resetAllSkip**: すべてのスキップをリセット

## 6. フロントエンド実装詳細

### 6.1 サーバーサイドレンダリング (SSR)
- Expressサーバーを使用したSSR実装
- クライアントとサーバーでコードを共有
- ディレクトリ構造:
  - `src/cljs/`: 共有コード
  - `src/cljs-server/`: サーバーサイド固有のコード
  - `src/cljs-client/`: クライアントサイド固有のコード

### 6.2 Service Worker
- workboxを使用したPWA実装
- オフライン対応とキャッシュ戦略
  - HTML: Network First戦略
  - 外部リソース: Cache First戦略
- Web Workerを使用した非同期API通信
  - メインスレッドをブロックせずにバックグラウンドでAPI呼び出し
  - `src/cljs-worker/`: Web Worker関連のコード
  - re-frameのエフェクトシステムと統合

### 6.3 マニフェストファイル
- PWA対応のためのmanifest.json
- ホーム画面への追加機能
- テーマカラー: #ef5350
- アイコン設定

### 6.4 ルーティング
- secretary.jsを使用したクライアントサイドルーティング
- 遅延ロード機能によるコード分割
- ルート変更時のGoogle Analytics連携

## 7. デプロイメント

### 7.1 開発環境
- Docker Composeを使用したローカル開発環境

### 7.2 CI/CD
- GitHub Actionsを使用した自動ビルドとデプロイ
- フロントエンド/バックエンドのコンテナイメージビルド
- ステージング環境への自動デプロイ
- 本番環境へのデプロイ

### 7.3 本番環境
- Kubernetesクラスタ上での運用
- スケーラビリティとフォールトトレランスの確保
- バックエンドとフロントエンドの分離デプロイ
