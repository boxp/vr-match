# SSRサーバー起動問題 修正計画

## 問題の原因

Renovateの自動依存関係更新により、Node.js 8 (Dockerfileで使用) と非互換なパッケージがmasterにマージされた。

### 非互換パッケージ一覧

| パッケージ | 現バージョン | 必要なNode.js | Dockerfile Node.js |
|---|---|---|---|
| `npm-run-all2` | 7.0.2 | >=18.17.0 | 8.17 |
| `nodemon` | 3.1.11 | >=10 | 8.17 |

### 追加の問題

- `firebase@5.11.1` が `grpc@1.20.0` (C++ネイティブモジュール) に依存
- Node.js 18用のプリビルドバイナリが存在せず、ソースビルドも旧API使用のため失敗する
- → Node.jsバージョンアップ時にfirebaseもアップグレードが必要

## 修正方針

### 1. Node.js 18 LTSへのアップグレード

**Dockerfile** (`front-end/Dockerfile`):
- `node:8.17-alpine` → `node:18-alpine` に変更（npmステージ、最終ステージ両方）

**理由**:
- `npm-run-all2@7.0.2` は Node.js 18.17以上を要求
- Node.js 8 は2019年12月にEOL
- Node.js 18 LTSはセキュリティサポートあり

### 2. firebase パッケージのアップグレード

**package.json**:
- `firebase@5.11.1` → `firebase@9.23.0` (v9系最終安定版)
- v9系は `@grpc/grpc-js` (純粋JavaScript実装) を使用し、ネイティブgrpcモジュールへの依存なし

**影響範囲**:
- SSRサーバーはfirebase SDKを直接使用しない（環境変数からconfigを読んでHTMLに埋め込むのみ）
- クライアントサイドはCLJSJSラッパー (`cljsjs/firebase "5.7.3-1"`) 経由でfirebaseを参照
- CLJSJSラッパーはexterns経由でグローバル変数を参照するため、npmパッケージバージョンとの直接的な依存は薄い
- ただし、firebase v9はModular API形式が推奨されるが、compat層 (`firebase/compat`) 経由で旧API (Namespaced API) も利用可能

### 3. .node-version ファイルの追加

ローカル開発環境でもNode.js 18を使用することを明示するため、`.node-version` ファイルを追加。

### 4. ssr-preamble.js の更新

firebase v9ではfetchポリフィルの問題が解消されているため、selfワークアラウンドの確認・更新。

## 変更ファイル一覧

1. `front-end/Dockerfile` - Node.js 18へのアップグレード
2. `front-end/package.json` - firebase バージョン更新
3. `front-end/package-lock.json` - npm installで自動更新
4. `front-end/.node-version` - 新規追加
5. `front-end/resources/ssr-preamble.js` - 必要に応じて更新
