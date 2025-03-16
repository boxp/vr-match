# Hito Hub プロジェクト コーディングルール

## 1. 全般的なルール

### 1.1 ファイル・ディレクトリ命名規則

- **ファイル名**: スネークケース（snake_case）を使用
  - 例: `user_repository.clj`, `profile_image.cljs`
- **ディレクトリ名**: スネークケース（snake_case）を使用
  - 例: `domain/entity/`, `lib/components/`
- **名前空間（namespace）**: ハイフン区切り（kebab-case）を使用
  - 例: `vr-match.lib.components`, `vr-match-back-end.domain.entity`

### 1.2 コード構造

- **単一責任の原則**: 各ファイルは単一の責任を持つように設計
- **関連機能のグループ化**: 関連する機能はディレクトリでグループ化
- **共通コンポーネント**: 共通コンポーネントは `lib` ディレクトリに配置
- **コメント**: 複雑なロジックには適切なコメントを付ける
- **インデント**: 2スペースを使用

### 1.3 バージョン管理

- **コミットメッセージ**: 簡潔かつ明確に変更内容を記述
- **ブランチ戦略**: 
  - 機能開発: `feature/<機能名>`
  - バグ修正: `fix/<バグ内容>`
  - リリース: `release/<バージョン>`

## 2. フロントエンド（ClojureScript）

### 2.1 名前空間（Namespace）規則

- **基本名前空間**: `vr-match`
- **機能モジュール**: `vr-match.<機能名>`
  - 例: `vr-match.auth`, `vr-match.mypage`
- **共通ライブラリ**: `vr-match.lib.<カテゴリ>`
  - 例: `vr-match.lib.components`, `vr-match.lib.utils`
- **モデル**: `vr-match.lib.models.<モデル名>`
  - 例: `vr-match.lib.models.me`, `vr-match.lib.models.platform`
- **名前空間の宣言**: 必要なモジュールのみをrequireし、アルファベット順に並べる

```clojure
(ns vr-match.lib.components.header
  (:refer-clojure :exclude [subs])
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]
            [reagent.core :as r]
            [vr-match.events :as events]
            [vr-match.lib.components.drawer :refer [drawer]]
            [vr-match.lib.components.material-ui :as mui]
            [vr-match.lib.models.me :as me]
            [vr-match.subs :as subs]))
```

### 2.2 コンポーネント命名規則

- **コンポーネント関数名**: キャメルケース（camelCase）を使用
- **プライベート関数**: 名前の前に `-` を付ける
  - 例: `-header-component`, `-format-date`
- **公開コンポーネント**: `defn` で定義
- **内部使用のみのコンポーネント**: `defn-` で定義
- **コンポーネントの引数**: destructuringを活用
  - 例: `[{:keys [title isOpen me] :as props}]`

### 2.3 Spec定義規則

- **公開関数**: すべての公開関数には `cljs.spec.alpha` による仕様を定義
- **型指定**: 入力パラメータと戻り値の型を明示的に指定
- **モデル仕様**: モデルの仕様は対応するモデル名前空間で定義
- **命名規則**: 仕様名は `::` プレフィックスを使用

```clojure
(s/def ::title string?)
(s/def ::isOpen boolean?)
(s/def ::me ::me/me)
(s/def ::header-component-props
  (s/keys :req-un [::title ::isOpen ::me]
          :opt-un [::handleOpenDrawer]))
(s/fdef header-component
  :args (s/cat :props ::header-component-props)
  :ret vector?)
```

### 2.4 Re-frame規則

- **イベントハンドラ**: `::` プレフィックスを使用
  - 例: `::initialize`, `::push`
- **サブスクリプション**: `::` プレフィックスを使用
  - 例: `::active-user`, `::loading?`
- **エフェクト/コエフェクト**: 専用の名前空間で定義
  - `effects.cljs`, `coeffects.cljs`
- **DB構造**: `db.cljs` で一元管理
- **イベント登録**: 適切なイベントハンドラを使用
  - `reg-event-db`: DBのみを更新
  - `reg-event-fx`: 副作用を伴う処理

```clojure
(re-frame/reg-event-db
 ::open-drawer
 (fn [db _]
   (assoc-in db [:drawer :open?] true)))

(re-frame/reg-event-fx
 ::push
 (fn [_ [_ path]]
   {::effects/route [path]}))
```

### 2.5 スタイリング規則

- **Material-UI**: コンポーネントのスタイリングにはMaterial-UIを使用
- **スタイル定義**: コンポーネント内でインラインスタイルを定義
- **テーマ**: グローバルテーマを使用して一貫性を保つ
- **レスポンシブデザイン**: モバイルファーストのアプローチを採用

## 3. バックエンド（Clojure）

### 3.1 名前空間（Namespace）規則

- **基本名前空間**: `vr-match-back-end`
- **クリーンアーキテクチャ階層**:
  - アプリケーション層: `vr-match-back-end.app.<モジュール名>`
    - 例: `vr-match-back-end.app.my-webapp.handler`
  - ドメイン層: `vr-match-back-end.domain.<カテゴリ>`
    - 例: `vr-match-back-end.domain.entity.user`
  - インフラ層: `vr-match-back-end.infra.<カテゴリ>`
    - 例: `vr-match-back-end.infra.repository.user`

### 3.2 エンティティ定義規則

- **エンティティ配置**: `vr-match-back-end.domain.entity.<エンティティ名>` で定義
- **仕様定義**: すべてのエンティティには `clojure.spec.alpha` による仕様を定義
- **キーワード命名**: データベースのカラム名に合わせたキーワード名を使用
  - 例: `::firebase_id`, `::created_at`
- **エンティティ構造**: 必須フィールドと任意フィールドを明示

```clojure
(s/def ::id number?)
(s/def ::firebase_id string?)
(s/def ::name string?)
(s/def ::introduction string?)
(s/def ::created_at ::t-spec/date-time)
(s/def ::updated_at ::t-spec/date-time)

(s/def ::user
  (s/keys :req-un [::id ::name ::introduction]
          :opt-un [::firebase_id ::created_at ::updated_at]))
```

### 3.3 リポジトリ実装規則

- **リポジトリ配置**: `vr-match-back-end.infra.repository.<リポジトリ名>` で定義
- **プロトコル分離**: インターフェースと実装を分離
- **依存性注入**: コンポーネントベースの依存性注入を使用
- **トランザクション**: 複数の操作を伴う場合はトランザクションを使用
- **例外処理**: 適切な例外処理とエラーハンドリングを実装

```clojure
(defprotocol UserRepository
  (find-by-id [this id])
  (find-all [this])
  (save [this user])
  (delete [this id]))

(defrecord MySQLUserRepository [db-spec]
  UserRepository
  (find-by-id [_ id]
    ;; 実装
    )
  ;; 他のメソッド実装
  )
```

### 3.4 GraphQL解決子（Resolver）規則

- **解決子配置**: `vr-match-back-end.app.<モジュール名>.resolvers` で定義
- **単一責任**: 各解決子は単一の責任を持つ純粋関数として実装
- **エラーハンドリング**: 明示的なエラーハンドリングを行う
- **認証/認可**: 必要に応じて認証/認可チェックを実装
- **データ変換**: エンティティとGraphQLレスポンスの変換を明確に定義

```clojure
(defn approach-list
  [context args value]
  (let [session (get context :session)
        limit (get args :limit 10)
        offset (get args :offset 0)]
    ;; 実装
    ))
```

## 4. データベース規則

### 4.1 テーブル命名規則

- **命名形式**: スネークケース（snake_case）を使用
- **単数形**: 複数形ではなく単数形を使用
  - 例: `user`, `platform` (users, platformsではない)
- **関連テーブル**: 両方のテーブル名を結合
  - 例: `user_platform`, `user_image`

### 4.2 カラム命名規則

- **命名形式**: スネークケース（snake_case）を使用
- **主キー**: `id` を使用
- **外部キー**: `<テーブル名>_id` の形式
  - 例: `user_id`, `platform_id`
- **タイムスタンプ**: `created_at`, `updated_at` を標準使用
- **真偽値**: `is_` または `has_` プレフィックスを使用
  - 例: `is_active`, `has_verified`

### 4.3 インデックス命名規則

- **主キーインデックス**: `pk_<テーブル名>`
- **外部キーインデックス**: `fk_<テーブル名>_<参照テーブル名>`
- **一意インデックス**: `uq_<テーブル名>_<カラム名>`
- **通常インデックス**: `idx_<テーブル名>_<カラム名>`

## 5. テスト規則

### 5.1 テストファイル構造

- **配置**: 対応するソースファイルと同じ名前空間構造に配置
- **ファイル名**: `<元ファイル名>_test.clj(s)` の形式
  - 例: `user_repository.clj` → `user_repository_test.clj`
- **テストフレームワーク**: clojure.test を使用

### 5.2 テスト命名規則

- **テスト関数名**: `test-<テスト対象関数>-<テストケース>` の形式
  - 例: `test-find-by-id-existing-user`, `test-save-new-user`
- **記述スタイル**: 期待される動作を明確に記述
- **フィクスチャ**: テストデータは適切に分離して定義

```clojure
(deftest test-find-by-id-existing-user
  (let [repo (->MockUserRepository)
        user (find-by-id repo 1)]
    (is (= 1 (:id user)))
    (is (= "Test User" (:name user)))))
```

### 5.3 テストカバレッジ

- **ユニットテスト**: すべての公開関数にはユニットテストを作成
- **統合テスト**: 重要なユースケースには統合テストを作成
- **エッジケース**: エッジケースと例外処理のテストを含める
- **モック/スタブ**: 外部依存はモックまたはスタブを使用

## 6. ドキュメント規則

### 6.1 コードドキュメント

- **関数ドキュメント**: 公開関数には docstring を付ける
- **名前空間ドキュメント**: 各名前空間の先頭にはその目的を記述
- **複雑なロジック**: 複雑なロジックには適切なコメントを付ける

```clojure
(ns vr-match-back-end.domain.entity.user
  "ユーザーエンティティの定義と関連する仕様")

(defn validate-user
  "ユーザーオブジェクトがバリデーションルールに従っているかを検証する
   
   引数:
     user - 検証対象のユーザーマップ
   
   戻り値:
     バリデーション結果のマップ {:valid? boolean :errors [string]}"
  [user]
  ;; 実装
  )
```

### 6.2 プロジェクトドキュメント

- **README**: プロジェクトの概要、セットアップ手順、基本的な使用方法を記述
- **アーキテクチャドキュメント**: システム全体のアーキテクチャを説明
- **API仕様**: GraphQL APIの仕様を詳細に記述
- **開発ガイド**: 開発環境のセットアップと開発フローを説明

## 7. CI/CD規則

### 7.1 ビルドプロセス

- **自動ビルド**: プルリクエスト作成時に自動ビルドを実行
- **テスト実行**: すべてのテストを実行し、結果を報告
- **コードスタイルチェック**: linterを使用してコードスタイルをチェック
- **依存関係チェック**: 脆弱性のある依存関係をチェック

### 7.2 デプロイメントプロセス

- **環境分離**: 開発、ステージング、本番環境を明確に分離
- **自動デプロイ**: マージ後に自動的にステージング環境にデプロイ
- **手動承認**: 本番環境へのデプロイは手動承認後に実行
- **ロールバック**: 問題発生時の迅速なロールバック手順を定義
