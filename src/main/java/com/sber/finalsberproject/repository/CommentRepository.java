package com.sber.finalsberproject.repository;

import com.sber.finalsberproject.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends GenericRepository<Comment> {
    @Query(nativeQuery = true,
            value = """
                    select distinct c.*
                    from comments c
                    where c.user_id = :taskId
                    """)
    Page<Comment> AllByUserId(Long taskId,
                              Pageable pageRequest);

    @Query(nativeQuery = true,
            value = """
                    select distinct c.*
                    from comments c
                    where c.task_id = :taskId
                    """)
    List<Comment> AllByTaskId(Long taskId);

    Page<Comment> searchCommentByTextOrAndCreatedWhen(@Param(value = "text") String text,
                                                      @Param(value = "created_when") LocalDateTime createdWhen,
                                                      Pageable pageRequest);
}
