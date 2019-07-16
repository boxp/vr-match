-- :name insert-user_platform :! :n
-- :doc user_platformを追加
insert into user_platform (user_id, platform_id, platform_user_id)
values (:user_id, :platform_id, :platform_user_id)

-- :name insert-user_platform-tuple :! :n
insert into user_platform (user_id, platform_id, platform_user_id)
values :t*:platforms

-- :name user_platform-by-user_id :? :*
-- :doc user_idと一致するuser_platformを取得
select platform.id as id,
       platform.name as name,
       platform.url_template as url_template,
       user_platform.platform_user_id as platform_user_id
from user_platform
inner join platform
on user_platform.platform_id = platform.id
where user_platform.user_id = :user_id

-- :name delete-user_platform-by-user_id :! :n
-- :doc user_idと一致するuser_platformを削除
delete
from user_platform
where user_id = :user_id
