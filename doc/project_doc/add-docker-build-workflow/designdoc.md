# Docker ビルド確認ワークフロー設計書

## 1. 概要

全てのブランチでフロントエンド（ClojureScript）とバックエンド（Clojure）それぞれのDockerビルドを行い、ビルドが正常に完了することを確認するGitHub Workflowを実装します。このワークフローは、コードの変更がDockerイメージのビルドを破壊していないことを早期に検出し、継続的インテグレーションの一環として機能します。

## 2. 目的

- 全てのブランチでのコード変更がDockerビルドを破壊していないことを確認する
- フロントエンドとバックエンドのDockerビルドを個別に検証する
- プルリクエスト作成時やコードプッシュ時に自動的にビルドチェックを実行する
- ビルド失敗時に早期に開発者に通知する

## 3. ワークフロー設計

### 3.1 ワークフロー名

- `docker-build-check.yml`

### 3.2 トリガー条件

- プルリクエスト作成時（`pull_request`）
- ブランチへのプッシュ時（`push`）
- 手動実行（`workflow_dispatch`）

### 3.3 対象ブランチ

- 全てのブランチ（`branches: ['**']`）

### 3.4 対象パス

- フロントエンドビルドジョブ: `front-end/**` の変更時
- バックエンドビルドジョブ: `back-end/**` の変更時
- 両方のジョブ: ワークフロー自体（`.github/workflows/docker-build-check.yml`）の変更時

### 3.5 ジョブ構成

#### 3.5.1 フロントエンドビルドジョブ（`build-frontend`）

- 実行環境: `ubuntu-latest`
- 条件: フロントエンド関連ファイルの変更時またはワークフロー自体の変更時
- ステップ:
  1. コードのチェックアウト
  2. Dockerビルドの実行（`docker build -t vr-match-frontend:test ./front-end`）
  3. ビルド結果の検証

#### 3.5.2 バックエンドビルドジョブ（`build-backend`）

- 実行環境: `ubuntu-latest`
- 条件: バックエンド関連ファイルの変更時またはワークフロー自体の変更時
- ステップ:
  1. コードのチェックアウト
  2. Dockerビルドの実行（`docker build -t vr-match-backend:test ./back-end`）
  3. ビルド結果の検証

### 3.6 ビルド結果の通知

- GitHub Actions上でのビルド結果表示
- プルリクエストへのビルド結果コメント（成功/失敗）
- ビルド失敗時のエラーログ表示

## 4. ワークフロー実装

```yaml
name: Docker Build Check

on:
  pull_request:
  push:
    branches: ['**']
  workflow_dispatch:

jobs:
  build-frontend:
    name: Build Frontend Docker Image
    runs-on: ubuntu-latest
    if: |
      github.event_name == 'workflow_dispatch' ||
      github.event_name == 'pull_request' ||
      github.event.head_commit && (
        contains(github.event.head_commit.modified, 'front-end/') ||
        contains(github.event.head_commit.added, 'front-end/') ||
        contains(github.event.head_commit.modified, '.github/workflows/docker-build-check.yml') ||
        contains(github.event.head_commit.added, '.github/workflows/docker-build-check.yml')
      )
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Build Frontend Docker image
        run: |
          docker build -t vr-match-frontend:test ./front-end
        
      - name: Verify build success
        run: |
          if [ "$(docker images -q vr-match-frontend:test 2> /dev/null)" == "" ]; then
            echo "Frontend Docker build failed"
            exit 1
          fi
          echo "Frontend Docker build succeeded"

  build-backend:
    name: Build Backend Docker Image
    runs-on: ubuntu-latest
    if: |
      github.event_name == 'workflow_dispatch' ||
      github.event_name == 'pull_request' ||
      github.event.head_commit && (
        contains(github.event.head_commit.modified, 'back-end/') ||
        contains(github.event.head_commit.added, 'back-end/') ||
        contains(github.event.head_commit.modified, '.github/workflows/docker-build-check.yml') ||
        contains(github.event.head_commit.added, '.github/workflows/docker-build-check.yml')
      )
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Build Backend Docker image
        run: |
          docker build -t vr-match-backend:test ./back-end
        
      - name: Verify build success
        run: |
          if [ "$(docker images -q vr-match-backend:test 2> /dev/null)" == "" ]; then
            echo "Backend Docker build failed"
            exit 1
          fi
          echo "Backend Docker build succeeded"
```

## 5. 改善点と拡張案

### 5.1 キャッシュの活用

ビルド時間を短縮するために、Dockerレイヤーのキャッシュを活用することを検討します。

```yaml
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v2

- name: Build and cache Frontend Docker image
  uses: docker/build-push-action@v4
  with:
    context: ./front-end
    push: false
    load: true
    tags: vr-match-frontend:test
    cache-from: type=gha
    cache-to: type=gha,mode=max
```

### 5.2 ビルドマトリックス

将来的に複数の環境（Node.jsバージョン、Javaバージョンなど）でのビルド検証が必要になった場合、ビルドマトリックスを活用して効率的にテストを実行できます。

### 5.3 テスト実行の統合

Dockerビルド後にコンテナを起動し、単体テストや統合テストを実行することで、より包括的な検証が可能になります。

### 5.4 セキュリティスキャン

Dockerイメージのセキュリティスキャンを統合し、脆弱性を早期に検出することを検討します。

```yaml
- name: Scan Docker image for vulnerabilities
  uses: aquasecurity/trivy-action@master
  with:
    image-ref: 'vr-match-frontend:test'
    format: 'table'
    exit-code: '1'
    severity: 'CRITICAL,HIGH'
```

## 6. 注意事項

- ワークフローの実行時間を最小限に抑えるため、変更があった場合のみ対応するビルドジョブを実行します
- プライベートリポジトリへのアクセスが必要な場合は、適切なシークレットを設定します
- ビルド時に必要な環境変数は、GitHub Secretsを使用して安全に管理します
- 大規模なDockerイメージのビルドはGitHub Actionsの実行時間制限に注意します

## 7. まとめ

このワークフローにより、全てのブランチでのコード変更がDockerビルドを破壊していないことを継続的に確認できます。フロントエンドとバックエンドのビルドを個別に検証することで、問題の特定が容易になり、開発プロセスの品質向上に貢献します。
