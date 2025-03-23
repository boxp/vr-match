# ビルドステージ
FROM node:14-alpine as build

# 作業ディレクトリの作成
WORKDIR /usr/src/app

# パッケージ依存関係のインストール
COPY package.json package-lock.json ./
RUN npm ci

# ソースコードと設定ファイルのコピー
COPY shadow-cljs.edn workbox-config.js ./
COPY src/ ./src/
COPY resources/ ./resources/

# プロダクションビルドの実行
RUN npm run build:prod

# 実行ステージ
FROM node:14-alpine

# 作業ディレクトリの作成
WORKDIR /usr/src/app

# ビルドステージから必要なファイルをコピー
COPY --from=build /usr/src/app/node_modules ./node_modules
COPY --from=build /usr/src/app/target ./target
COPY --from=build /usr/src/app/resources/public/prod ./resources/public/prod

# アプリケーションの実行
CMD ["node", "target/server/prod/js/compiled/server.js", "3000"]
