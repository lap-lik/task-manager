UPDATE comments
SET
    avatar_online_copy_path = (SELECT online_copy_path1 FROM users WHERE comments.user_id = users.id) and
                              where user_id = 5;




