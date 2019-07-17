-- :name insert-user_skip :! :n
-- :doc user_skipの追加
insert into user_skip (from_id, to_id)
values (:from_id, :to_id)
