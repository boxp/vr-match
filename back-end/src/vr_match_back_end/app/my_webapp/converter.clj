(ns vr-match-back-end.app.my-webapp.converter
  (:require
   [clojure.spec.alpha :as s]
   [clojure.set :as set]
   [clojure.data.codec.base64 :as b64]
   [clj-time.format :as f]
   [vr-match-back-end.domain.entity.user :as euser]
   [vr-match-back-end.domain.entity.platform :as eplatform]))

(s/fdef platform->Platform
  :args (s/cat :platform ::eplatform/platform)
  :ret map?)
(defn platform->Platform
  [platform]
  (-> platform
      (set/rename-keys {:platform-user-id :platformUserId})))

(s/fdef user->User
  :args (s/cat :user ::euser/user)
  :ret map?)
(defn user->User
  [user]
  (-> user
      (dissoc :firebase_id)
      (dissoc :session_cookie)
      (dissoc :created_at)
      (dissoc :updated_at)
      (set/rename-keys {:matched? :isMatched})
      (set/rename-keys {:favorited? :isFavorited})))

(def date-time-formatter (f/formatters :basic-date-time))

(defn- convert-local-date-time-to-joda
  "Convert java.time.LocalDateTime to org.joda.time.DateTime"
  [local-date-time]
  (org.joda.time.DateTime.
    (.getYear local-date-time)
    (.getMonthValue local-date-time)
    (.getDayOfMonth local-date-time)
    (.getHour local-date-time)
    (.getMinute local-date-time)
    (.getSecond local-date-time)
    (.get local-date-time java.time.temporal.ChronoField/MILLI_OF_SECOND)))

(defn date-time->string
  "Convert date-time object to string"
  [date-time]
  (if (instance? java.time.LocalDateTime date-time)
    (let [joda-date-time (convert-local-date-time-to-joda date-time)]
      (f/unparse date-time-formatter joda-date-time))
    ;; else - assume it's already a joda DateTime or compatible
    (f/unparse date-time-formatter date-time)))

(defn string->date-time
  [string]
  (f/parse date-time-formatter string))

(defn decode-cursor
  [cursor]
  (-> cursor .getBytes b64/decode String.))

(defn encode-cursor
  [string]
  (-> string .getBytes b64/encode String.))
