package com.sber.finalsberproject.service;

import com.sber.finalsberproject.constants.Errors;
import com.sber.finalsberproject.dto.AddUserDTO;
import com.sber.finalsberproject.dto.CompanyDTO;
import com.sber.finalsberproject.exception.MyDeleteException;
import com.sber.finalsberproject.mapper.CompanyMapper;
import com.sber.finalsberproject.model.Company;
import com.sber.finalsberproject.model.User;
import com.sber.finalsberproject.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
@Slf4j
@Service
public class CompanyService extends GenericService<Company, CompanyDTO>{
    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        super(companyRepository, companyMapper);
    }

    public CompanyDTO addUser(final AddUserDTO addUserDTO) {
        CompanyDTO company = getOne(addUserDTO.getCompanyId());
        company.getUsersId().add(addUserDTO.getUserId());
        update(company);
        return company;
    }

    public Page<CompanyDTO> searchCompany(final String title,
                                          Pageable pageable) {
        Page<Company> theCompany = ((CompanyRepository)repository).findAllByTitleContainsIgnoreCaseAndDeletedFalse(title, pageable);
        List<CompanyDTO> result = mapper.toDTOs(theCompany.getContent());
        return new PageImpl<>(result, pageable, theCompany.getTotalElements());
    }

    @Override
    public void deleteSoft(Long companyId) throws MyDeleteException {
        Company company = repository.findById(companyId).orElseThrow(
                () -> new NotFoundException("Фирма с заданным id=" + companyId + " не существует."));
        boolean companyCanBeDeleted = ((CompanyRepository)repository).checkCompanyForDeletion(companyId);
        if (companyCanBeDeleted) {
            markAsDeleted(company);
            List<User> users = company.getUsers();
            if (users != null && users.size() > 0) {
                users.forEach(this::markAsDeleted);
            }
            ((CompanyRepository)repository).save(company);
        }
        else {
            throw new MyDeleteException(Errors.Company.COMPANY_DELETE_ERROR);
        }
    }

    public void restore(Long objectId) {
        Company company = repository.findById(objectId).orElseThrow(
                () -> new NotFoundException("Фирма с заданным id=" + objectId + " не существует."));
        unMarkAsDeleted(company);
        List<User> users = company.getUsers();
        if (users != null && users.size() > 0) {
            users.forEach(this::unMarkAsDeleted);
        }
        repository.save(company);
    }
}
