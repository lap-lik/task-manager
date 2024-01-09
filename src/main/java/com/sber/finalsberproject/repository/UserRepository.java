package com.sber.finalsberproject.repository;

import com.sber.finalsberproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends GenericRepository<User>{
    User findUserByLoginAndIsDeletedFalse(String login);
    User findUserByLogin(String login);
    User findUserByEmail(String email);

    @Query(nativeQuery = true,
            value = """
    select distinct u.*
    from users u inner join users_tasks ut on ut.user_id = u.id
    where ut.task_id = :taskId
    """)
    List<User> listUserByTaskId (Long taskId);

    List<User> findAllByCompanyId(Long id);

    @Query(nativeQuery = true,
            value = """
    select u.*
    from users u
    where u.company_id IS NULL and u.role_id = :roleId
    """)
    List<User> listAllByRoleAndCompany(Long roleId);

    @Query(nativeQuery = true,
            value = """
                 select u.*
                 from users u
                 where u.first_name ilike '%' || coalesce(:firstName, '%') || '%'
                 and u.last_name ilike '%' || coalesce(:lastName, '%') || '%'
                 and u.login ilike '%' || coalesce(:login, '%') || '%'
                  """)
    Page<User> searchUsers(String firstName,
                           String lastName,
                           String login,
                           Pageable pageable);
}
