# Hito Hub front-end

フロントエンドのためのプロジェクトです。
SSRを行うサーバーと、ブラウザ向けのコードが置かれています。

## Setup

このプロジェクトは以下のツールに依存しています。ビルド前にインストールしてください

- Leiningen
- nodejs & npm

### 依存ライブラリのインストール

git clone後やライブラリの変更がある場合は必ず実行してください

```
npm install
```

## 開発環境の起動

Port8888番でSSR用サーバーが立ち上がります

```
npm run watch
```

## ビルド

### 本番用

```
npm run build:prod
```

### 開発用差分ビルド

```
npm run figwheel
```

## コードフォーマット

```
npm run format
```
