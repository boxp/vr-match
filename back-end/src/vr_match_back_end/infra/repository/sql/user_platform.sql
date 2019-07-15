-- :name insert-user_platform :! :n
-- :doc user_platformを追加
insert into user_platform (user_id, platform_id, platform_user_id)
values (:user_id, :platform_id, :platform_user_id)

-- :name user_platform-by-user_id-with-platform :? :*
-- :doc user_idと一致するuser_platform,platformを取得
select platform.id as id,
       platform.name as name,
       platform.url_template as url_template,
       user_platform.platform_user_id as platform_user_id
from user_platform
where user_platform.user_id = :user_id
inner join platform
on user_platform.platform_id = platform.id
