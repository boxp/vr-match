FROM clojure
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/

# 依存関係のインストール
RUN lein deps

# ソースコードのコピーとビルド
COPY . /usr/src/app
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar

# ポート公開と実行コマンド
EXPOSE 8080
CMD ["java", "-Djava.security.policy=/usr/src/app/java.policy", "-cp", "app-standalone.jar", "vr_match_back_end.app.my_webapp.system"]
