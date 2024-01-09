package com.sber.finalsberproject.repository;
import com.sber.finalsberproject.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends GenericRepository<Company> {

    Page<Company> findAllByTitleContainsIgnoreCaseAndDeletedFalse(String title,
                                                                       Pageable pageable);

    @Query(value = """
          select case when count(u) > 0 then false else true end
          from User u
          where u.company = :companyId
          """)
    boolean checkCompanyForDeletion(final Long companyId);

}
