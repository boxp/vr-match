-- :name user-by-id :? :1
-- :doc idからUserを一件取得
select * from user
where id = :id

-- :name user-by-firebase_id :? :1
-- :doc firebase_idからUserを一件取得
select * from user
where firebase_id = :firebase_id

-- :name insert-user :i! :n
-- :doc Userを追加
insert into user (firebase_id, name, introduction)
values (:firebase_id, :name, :introduction)

-- :name update-user-by-id :! :n
/* :require [clojure.string :as string] */
update user
set
id = :id
/*~
(->> (-> params (dissoc :id))
     (map (fn [[key value]]
              (str "," key " = " value)))
     (apply str))
~*/
where id = :id
