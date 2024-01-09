INSERT INTO roles (id, description, role_title)
SELECT 1, 'Роль пользователя', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE id = 1)
UNION ALL
SELECT 2, 'Роль администратор', 'ADMINISTRATOR'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE id = 2);


select u.*
from users u
where u.company_id IS NULL
  and u.role_id = 1;

select case when count(u) > 0 then false else true end
from users u
where u.company_id = 2;

SELECT case when (COUNT(u) > 0, 'true', 'false')
FROM users u
WHERE u.company_id = 2;


select t.*
from tasks t
             inner join users_tasks ut on t.id = ut.task_id
             inner join users u on ut.user_id = u.id
where u.id = 1;

select distinct t.*
from tasks t inner join users_tasks ut on ut.task_id = t.id
where ut.task_id = 2;

select distinct u.*
from users u inner join users_tasks ut on ut.user_id = u.id
where ut.task_id = 2;


select t.*
from tasks t inner join users_tasks ut on t.id = ut.task_id
where ut.user_id = 2
ORDER BY id DESC LIMIT 1;


select distinct t.*
from tasks t inner join users_tasks ut on ut.task_id = t.id
where ut.user_id = 1
  and t.pattern = 1 and t.task_id is null and t.is_completed = false