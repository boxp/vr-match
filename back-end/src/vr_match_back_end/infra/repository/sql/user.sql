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
with CommonPlatforms as (
  select platform_id
  from user_platform
  where user_id = :user_id
)
select user.id, user.name, user.introduction
from user
JOIN user_platform ON user.id = user_platform.user_id
left join CommonPlatforms on user_platform.platform_id = CommonPlatforms.platform_id
left join user_image on user.id = user_image.user_id
left join user_skip on user.id = user_skip.to_id and user_skip.from_id = :user_id
left join user_favorite on user.id = user_favorite.to_id and user_favorite.from_id = :user_id
where user.id != :user_id
and user_skip.to_id is null
and user_favorite.to_id is null
and user.id not in (:v*:exclude_ids)
group by user.id, user.name, user.introduction, user_platform.user_id, user_image.user_id
order by count(user_image.image_id) desc, count(user_platform.platform_id) desc, user.created_at desc
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
and user.id not in (:v*:exclude_ids)

-- :name user_with_status :? :1
-- :doc parnter_idからme_idに対してのマッチング情報・お気に入り情報を含むUser一件取得
WITH FavoriteStats AS (
    SELECT
        CASE 
            WHEN to_id = :me_id THEN from_id
            WHEN from_id = :me_id THEN to_id
        END AS user_id,
        COUNT(*) AS total_count,
        MAX(from_id = :me_id) AS is_favorited_from_me
    FROM user_favorite
    WHERE :me_id IN (from_id, to_id)
    GROUP BY user_id
)
SELECT
    u.id AS id,
    u.name AS name,
    u.introduction AS introduction,
    fs.total_count >= 2 AS is_matched,
    fs.is_favorited_from_me AND fs.total_count >= 2 AS is_favorited_from_me_corrected
FROM user u
LEFT JOIN FavoriteStats fs ON u.id = fs.user_id
WHERE u.id = :partner_id;
