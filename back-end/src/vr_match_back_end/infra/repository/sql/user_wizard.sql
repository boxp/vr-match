-- :name user_wizard-by-user_id :? :*
-- :doc user_idと合致するuser_wizardを返します
select wizard_step
from user_wizard
where user_id = :user_id

-- :name insert-user_wizard :! :n
-- :doc user_wizardをinsert
insert into user_wizard (user_id, wizard_step, wizard_complete)
values (:user_id, :wizard_step, :wizard_complete)
