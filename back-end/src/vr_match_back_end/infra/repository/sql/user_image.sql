-- :name insert-user_image :! :n
-- :doc user_imageを追加
insert into user_image (user_id, image_id, image_type)
values (:user_id, :image_id, :image_type)

-- :name count-user_image-by-user_id-with-image_type :? :1
-- :doc image_typeと合致するuser_idの画像の枚数を取得
select count(*) as image_count from user_image
where user_id = :user_id
and image_type = :image_type
