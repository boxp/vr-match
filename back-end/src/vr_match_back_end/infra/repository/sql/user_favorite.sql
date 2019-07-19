-- :name insert-user_favorite :! :n
-- :doc user_favoriteの追加
insert into user_favorite (from_id, to_id)
values (:from_id, :to_id)

-- :name favorited-user-by-user_id :? :*
-- :doc user_idからお気に入りされているお相手を取得
select
user.id as id,
user.name as name,
user.introduction as introduction,
user_favorite.created_at as created_at
from user_favorite
inner join user on user_favorite.to_id = user.id
where user_favorite.from_id = :user_id
and user_favorite.created_at < :after
order by user_favorite.created_at desc
limit :limit

-- :name count-favorited-user-by-user_id :? :1
-- :doc user_idからお気に入りされているお相手の人数を取得
select count(*) as total
from user_favorite
inner join user on user_favorite.to_id = user.id
where user_favorite.from_id = :user_id

-- :name count-user_favorite-by-each-other-id :? :1
-- :doc partner_idとme_idの間で存在するuser_favoriteの数を返す
select count(*) as total
from user_favorite
where (from_id = :partner_id and to_id = :me_id)
or (from_id = :me_id and to_id = :partner_id)
