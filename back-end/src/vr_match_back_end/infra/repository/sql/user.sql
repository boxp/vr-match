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
-- :doc Userをparamsを元に更新
/* :require [hugsql.parameters :refer [identifier-param-quote]] */
update user
set
id = :id
/*~
(->> (-> params (dissoc :id))
     (map (fn [[key _]]
              (str "," (identifier-param-quote (name key) options) " = :" (name key))))
     (apply str))
~*/
where id = :id

-- :name recommended-user-by-user_id :? :*
-- :doc user_idのuserに対してまだお気に入りもスキップもしていないユーザーをおすすめ順に取得
select user.id, user.name, user.introduction
from user
left join user_platform on user.id = user_platform.user_id and user_platform.platform_id in (
  select user_platform.platform_id
  from `user`
  inner join user_platform on user.id = user_platform.user_id
  where user.id = :user_id
)
left join user_image on user.id = user_image.user_id
left join user_skip on user.id = user_skip.to_id and user_skip.from_id = :user_id
left join user_favorite on user.id = user_favorite.to_id and user_favorite.from_id = :user_id
where user.id != :user_id
and user_skip.to_id is null
and user_favorite.to_id is null
group by user.id, user.name, user.introduction, user_platform.user_id, user_image.user_id
order by count(user_platform.platform_id) desc, count(user_image.image_id) desc, user.created_at desc
limit :offset, :limit

-- :name count-recommended-user-by-user_id :? :1
-- :doc user_idのuserに対しておすすめのユーザーの人数を取得
select count(*) as total
from user
left join user_skip on user.id = user_skip.to_id and user_skip.from_id = :user_id
left join user_favorite on user.id = user_favorite.to_id and user_favorite.from_id = :user_id
where user.id != :user_id
and user_skip.to_id is null
and user_favorite.to_id is null

-- :name user-with-is_matched :? :1
-- :doc parnter_idからme_idに対してのマッチング情報を含むUser一件取得
select user.id as id, user.name as name, user.introduction as introduction, count(user.id) >= 2 as is_matched
from `user`
inner join user_favorite
on (user.id = user_favorite.to_id and :me_id = user_favorite.from_id)
or (user.id = user_favorite.from_id and :me_id = user_favorite.to_id)
where user.id = :partner_id
