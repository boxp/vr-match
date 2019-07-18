-- :name insert-user_favorite :! :n
-- :doc user_favoriteの追加
insert into user_favorite (from_id, to_id)
values (:from_id, :to_id)
