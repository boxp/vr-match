# Hito Hub 仕様書

## 概要

Hito Hubは、VRアバターのためのマッチングサービスです。ユーザーはVRプラットフォーム上で使用するアバターの情報を登録し、他のユーザーとマッチングすることができます。

## システム構成

### アーキテクチャ

- マイクロサービスアーキテクチャを採用
- フロントエンド: ClojureScript + re-frame + React.js
- バックエンド: Clojure（クリーンアーキテクチャ）
- データベース: MySQL
- 認証: Firebase Authentication
- ストレージ: Cloud Storage
- API: GraphQL

## 機能仕様

### ユーザー管理

#### ユーザー登録・認証
- Firebase Authenticationを使用
- メールアドレス登録
- Twitterログイン
- セッション管理（トークンベース）

#### ユーザープロフィール
- 基本情報
  - 名前
  - 自己紹介文
- プロフィール画像
  - メイン画像と複数のサブ画像をアップロード可能
- プラットフォーム情報
  - 複数のVRプラットフォームのユーザーIDを登録可能

### マッチング機能

#### アプローチ機能
- 他のユーザーを閲覧
- お気に入り登録（いいね）
- スキップ（興味なし）
- 相互いいねでマッチング成立

#### マッチング管理
- マッチングしたユーザー一覧表示
- お気に入りに登録したユーザー一覧表示
- 自分をお気に入りに登録したユーザー一覧表示
- スキップしたユーザーを再表示する機能

### 画面構成

#### 認証関連
- ウェルカム画面
- 登録画面
- メール登録画面
- メール登録完了画面
- Twitterログイン画面
- メールログイン画面
- メールログイン完了画面

#### プロフィール設定
- ウィザード画面（初回登録時）
- マイプロフィール編集画面
- 設定画面
- サードパーティ認証設定画面

#### マッチング関連
- アプローチ画面（他ユーザー閲覧）
- お気に入りユーザー一覧画面
- お気に入りされたユーザー一覧画面
- マッチング成立ユーザー一覧画面
- ユーザープロフィール詳細画面
- マイページ

## データモデル

### ユーザー (user)
- id: ユーザーID
- firebase_id: Firebase認証ID
- name: ユーザー名
- introduction: 自己紹介
- created_at: 作成日時
- updated_at: 更新日時

### プラットフォーム (platform)
- id: プラットフォームID
- name: プラットフォーム名
- url_template: URLテンプレート
- example_user_id: サンプルユーザーID
- created_at: 作成日時
- updated_at: 更新日時

### ユーザープラットフォーム (user_platform)
- user_id: ユーザーID
- platform_id: プラットフォームID
- platform_user_id: プラットフォーム上のユーザーID
- created_at: 作成日時
- updated_at: 更新日時

### 画像 (image)
- id: 画像ID
- url: 画像URL
- placeholder_color: プレースホルダーカラー
- created_at: 作成日時
- updated_at: 更新日時

### ユーザー画像 (user_image)
- user_id: ユーザーID
- image_id: 画像ID
- type: 画像タイプ（メイン/サブ）
- created_at: 作成日時
- updated_at: 更新日時

### ユーザーお気に入り (user_favorite)
- user_id: お気に入りしたユーザーID
- favorite_user_id: お気に入りされたユーザーID
- created_at: 作成日時
- updated_at: 更新日時

### ユーザーマッチ (user_match)
- user_id: ユーザーID
- match_user_id: マッチしたユーザーID
- created_at: 作成日時
- updated_at: 更新日時

### ユーザースキップ (user_skip)
- user_id: スキップしたユーザーID
- skip_user_id: スキップされたユーザーID
- created_at: 作成日時
- updated_at: 更新日時

## API仕様

GraphQLベースのAPIを提供しています。

### 主要なクエリ
- approachList: アプローチ可能なユーザーリスト取得
- favoritedUsers: お気に入りユーザーリスト取得
- matchedUsers: マッチングしたユーザーリスト取得
- favoritedFromUsers: 自分をお気に入りに登録したユーザーリスト取得
- me: 自分のプロフィール取得
- partner: 特定ユーザーのプロフィール取得
- platformOptions: 利用可能なプラットフォーム一覧取得

### 主要なミューテーション
- registerUser: ユーザー登録
- loginUser: ログイン
- uploadImage: 画像アップロード
- updateMe: プロフィール更新
- skip: ユーザースキップ
- favorite: ユーザーをお気に入りに登録
- resetAllSkip: すべてのスキップをリセット

## 技術スタック

### フロントエンド
- ClojureScript
- reagent (React.jsラッパー)
- re-frame (Flux/Reduxライクな状態管理)
- material-ui (UIコンポーネント)
- workbox (PWA対応)

### フロントエンド実装詳細

#### サーバーサイドレンダリング (SSR)
- Expressサーバーを使用したSSR実装
- クライアントとサーバーでコードを共有
- ディレクトリ構造:
  - `src/cljs/`: 共有コード
  - `src/cljs-server/`: サーバーサイド固有のコード
  - `src/cljs-client/`: クライアントサイド固有のコード

#### Service Worker
- workboxを使用したPWA実装
- オフライン対応とキャッシュ戦略
  - HTML: Network First戦略
  - 外部リソース: Cache First戦略
- Web Workerを使用した非同期API通信
  - メインスレッドをブロックせずにバックグラウンドでAPI呼び出し
  - `src/cljs-worker/`: Web Worker関連のコード
  - re-frameのエフェクトシステムと統合

#### マニフェストファイル
- PWA対応のためのmanifest.json
- ホーム画面への追加機能
- テーマカラー: #ef5350
- アイコン設定

#### ルーティング
- secretary.jsを使用したクライアントサイドルーティング
- 遅延ロード機能によるコード分割
- ルート変更時のGoogle Analytics連携

### バックエンド
- Clojure
- クリーンアーキテクチャ
- GraphQL
- MySQL
- Firebase Authentication
- Cloud Storage

### インフラストラクチャ
- Kubernetes
- Docker
- GitHub Actions (CI/CD)

## 命名規則とコーディングルール

### 全般的なルール

#### ファイル・ディレクトリ命名規則
- ファイル名はスネークケース（snake_case）を使用
- ディレクトリ名もスネークケース（snake_case）を使用
- 名前空間（namespace）はハイフン区切り（kebab-case）を使用

#### コード構造
- 各ファイルは単一の責任を持つように設計
- 関連する機能はディレクトリでグループ化
- 共通コンポーネントは `lib` ディレクトリに配置

### フロントエンド（ClojureScript）

#### 名前空間（Namespace）規則
- 基本名前空間: `vr-match`
- 機能モジュール: `vr-match.<機能名>` (例: `vr-match.auth`, `vr-match.mypage`)
- 共通ライブラリ: `vr-match.lib.<カテゴリ>` (例: `vr-match.lib.components`)
- モデル: `vr-match.lib.models.<モデル名>` (例: `vr-match.lib.models.me`)

#### コンポーネント命名規則
- コンポーネント関数名はキャメルケース（camelCase）
- 公開コンポーネントは `defn` で定義
- 内部使用のみのコンポーネントは `defn-` で定義

#### Spec定義規則
- すべての公開関数には `cljs.spec.alpha` による仕様を定義
- 入力パラメータと戻り値の型を明示的に指定
- モデルの仕様は対応するモデル名前空間で定義

#### Re-frame規則
- イベントハンドラは `::` プレフィックスを使用 (例: `::initialize`)
- サブスクリプションも `::` プレフィックスを使用
- エフェクトとコエフェクトは専用の名前空間で定義
- DB構造は `db.cljs` で一元管理

### バックエンド（Clojure）

#### 名前空間（Namespace）規則
- 基本名前空間: `vr-match-back-end`
- クリーンアーキテクチャに従った階層:
  - アプリケーション層: `vr-match-back-end.app.<モジュール名>`
  - ドメイン層: `vr-match-back-end.domain.<カテゴリ>`
  - インフラ層: `vr-match-back-end.infra.<カテゴリ>`

#### エンティティ定義規則
- エンティティは `vr-match-back-end.domain.entity.<エンティティ名>` で定義
- すべてのエンティティには `clojure.spec.alpha` による仕様を定義
- データベースのカラム名に合わせたキーワード名を使用 (例: `::firebase_id`)

#### リポジトリ実装規則
- リポジトリは `vr-match-back-end.infra.repository.<リポジトリ名>` で定義
- プロトコルとその実装を分離
- 依存性注入パターンを使用

#### GraphQL解決子（Resolver）規則
- 解決子は `vr-match-back-end.app.<モジュール名>.resolvers` で定義
- 各解決子は単一の責任を持つ純粋関数として実装
- エラーハンドリングを明示的に行う

### データベース命名規則

#### テーブル名
- スネークケース（snake_case）を使用
- 複数形ではなく単数形を使用 (例: `user`, `platform`)
- 関連テーブルは両方のテーブル名を結合 (例: `user_platform`)

#### カラム名
- スネークケース（snake_case）を使用
- 主キーは `id`
- 外部キーは `<テーブル名>_id` (例: `user_id`, `platform_id`)
- タイムスタンプは `created_at`, `updated_at` を標準使用

### テスト規則

#### テストファイル構造
- テストファイルは対応するソースファイルと同じ名前空間構造に配置
- テストファイル名は `<元ファイル名>_test.clj(s)` の形式

#### テスト命名規則
- テスト関数名は `test-<テスト対象関数>-<テストケース>` の形式
- 期待される動作を明確に記述

#### テストカバレッジ
- すべての公開関数にはユニットテストを作成
- 重要なユースケースには統合テストを作成
- エッジケースと例外処理のテストを含める
