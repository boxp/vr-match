-- :name insert-image :i! :n
-- :doc Imageを追加
insert into image (url)
values (:url)

-- :name update-image-by-id :! :1
-- :doc Imageの更新
update image
set id = :id
    url = :url
where id = :id
