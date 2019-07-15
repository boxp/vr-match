-- :name insert-user_image :! :n
-- :doc user_imageを追加
insert into user_image (user_id, image_id, image_type)
values (:user_id, :image_id, :image_type)

-- :name image-by-user_id-and-image_type
-- :doc user_idとimage_typeで絞り込んでimageを取得
select
image.id as id,
image.url :as url,
user_image.image_type as image_type
from user_image
where user_image.user_id = :user_id
and user_image.image_type = :image_type
inner join image
on user_image.image_id = image.id

-- :name delete-user_image
-- :doc user_imageの削除
delete
from user_image
where user_id = :user_id
and image_id = :image_id

-- :name update-user_image-by-user_id-and-image_id
-- :doc user_imageのimage_idを更新
update user_image
set image_id = :image_id
where user_id = :user_id
and image_id = :image_id
