package com.sber.finalsberproject.repository;

import com.sber.finalsberproject.model.GenericModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface GenericRepository <T extends GenericModel> extends JpaRepository<T, Long> {
    Page<T> findAllByIsDeletedFalse (Pageable pageable);
    List<T> findAllByIsDeletedFalse();

}
