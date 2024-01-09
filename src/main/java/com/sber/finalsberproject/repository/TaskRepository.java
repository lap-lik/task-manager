package com.sber.finalsberproject.repository;

import com.sber.finalsberproject.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TaskRepository extends GenericRepository<Task> {


    @Query(nativeQuery = true,
            value = """
                    select distinct t.*
                    from tasks t inner join users_tasks ut on ut.task_id = t.id
                    where ut.user_id = :userId and t.pattern = :pattern
                    """)
    Page<Task> allTasksByUserByPattern(Long userId,
                                       Pageable pageRequest,
                                       Integer pattern);


    @Query(nativeQuery = true,
            value = """
                    select distinct t.*
                    from tasks t
                    where t.pattern = :pattern
                    """)
    Page<Task> allTasksByPattern(Pageable pageRequest,
                                 Integer pattern);

    @Query(nativeQuery = true,
            value = """
                    select distinct t.*
                    from tasks t
                    where t.pattern = :pattern and t.task_id is null and t.is_completed = false
                    """)
    List<Task> allTasksByPatternForMian(Integer pattern);


    @Query(nativeQuery = true,
            value = """
                    select distinct t.*
                    from tasks t inner join users_tasks ut on ut.task_id = t.id
                    where ut.user_id = :userId
                    and t.pattern = :pattern and t.task_id is null and t.is_completed = false
                    """)
    List<Task> listTasksByUserByPatternForMain(Long userId,
                                               Integer pattern);


    @Query(nativeQuery = true,
            value = """
                    select distinct t.*
                    from tasks t inner join users_tasks ut on ut.task_id = t.id
                    where t.pattern = 0\s
                    and ut.user_id = :userId
                    """)
    List<Task> AllBigTasksByUser(Long userId);


    @Query(nativeQuery = true,
            value = """
                    select t.*
                        from tasks t inner join users_tasks ut on t.id = ut.task_id
                        where ut.user_id = 2
                        ORDER BY id DESC LIMIT 1;
                    """)
    Task findLastTaskByUserId(Long id);


    @Query(nativeQuery = true,
            value = """
                    select distinct t.*
                    from tasks t inner join users_tasks ut on ut.task_id = t.id
                    where t.pattern = 2\s
                    and ut.user_id = :userId
                    """)
    List<Task> AllSmallTasksByUser(Long userId);


    @Query(nativeQuery = true,
            value = """
                    select distinct t.*
                    from tasks t inner join users_tasks ut on ut.task_id = t.id
                    where ut.user_id = :userId
                    """)
    Page<Task> AllTasksByUser(Long userId,
                              Pageable pageRequest);


    @Query(nativeQuery = true,
            value = """
                    select distinct t.*
                    from tasks t inner join users_tasks ut on ut.task_id = t.id
                    where t.pattern = 0\s
                    and ut.user_id = :userId
                    """)
    Page<Task> AllBigTasksByUser(Long userId,
                                 Pageable pageRequest);

    List<Task> findAllByTaskId(Long tasksId);

    @Query(nativeQuery = true,
            value = """
                    select distinct t.*
                    from tasks t inner join users_tasks ut on ut.task_id = t.id
                    where t.pattern = 1\s
                    and ut.user_id = :userId
                    """)
    List<Task> AllMediumTasksByUser(Long userId);


    @Query(nativeQuery = true,
            value = """
                    select distinct t.*
                    from tasks t inner join users_tasks ut on ut.task_id = t.id
                    where t.pattern = 1\s
                    and ut.user_id = :userId
                    """)
    Page<Task> AllMediumTasksByUser(Long userId,
                                    Pageable pageRequest);


    @Query(nativeQuery = true,
            value = """
                    select distinct t.*
                    from tasks t inner join users_tasks ut on ut.task_id = t.id
                    where t.pattern = 2\s
                    and ut.user_id = :userId
                    """)
    Page<Task> AllSmallTasksByUser(Long userId,
                                   Pageable pageRequest);


    @Query(nativeQuery = true,
            value = """
                       select distinct t.*
                       from tasks t
                       where t.task_title ilike '%' || coalesce(:task_title, '%')  || '%'
                         and cast(t.created_when as char) like coalesce(:created_when, '%')
                         and coalesce(t.completed_when, '%') ilike '%' ||  coalesce(:completed_when, '%')  || '%'
                         and t.is_deleted = false
                    """)
    Page<Task> searchTasks(@Param(value = "task_title") String taskTitle,
                           @Param(value = "created_when") LocalDateTime createdWhen,
                           @Param(value = "completed_when") LocalDateTime completedWhen,
                           Pageable pageRequest);
}
