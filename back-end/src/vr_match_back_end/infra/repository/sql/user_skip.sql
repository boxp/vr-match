-- :name insert-user_skip :! :n
-- :doc user_skipの追加
insert into user_skip (from_id, to_id)
values (:from_id, :to_id)

-- :name delete-all-user_skip-by-user_id :! :n
-- :doc user_idとfrom_idが一致するuser_skipを削除
delete
from user_skip
where from_id = :user_id
