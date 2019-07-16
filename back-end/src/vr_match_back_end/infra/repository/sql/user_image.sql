-- :name insert-user_image :! :n
-- :doc user_imageを追加
insert into user_image (user_id, image_id, image_type)
values (:user_id, :image_id, :image_type)

-- :name user_image-by-user_id :? :*
-- :doc user_idで絞り込んでuser_imageを取得
select
image.id as id,
image.url as url,
user_image.image_type as image_type
from user_image
inner join image
on user_image.image_id = image.id
where user_image.user_id = :user_id

-- :name delete-user_image :! :n
-- :doc user_imageの削除
delete
from user_image
where user_id = :user_id
and image_id = :image_id
