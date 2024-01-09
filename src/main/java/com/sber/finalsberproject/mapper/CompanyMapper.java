package com.sber.finalsberproject.mapper;

import com.sber.finalsberproject.dto.CompanyDTO;
import com.sber.finalsberproject.model.Company;
import com.sber.finalsberproject.model.GenericModel;
import com.sber.finalsberproject.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CompanyMapper extends GenericMapper<Company, CompanyDTO> {
    private final UserRepository userRepository;
    public CompanyMapper(ModelMapper modelMapper,
                         UserRepository userRepository) {
        super(modelMapper, Company.class, CompanyDTO.class);
        this.userRepository = userRepository;
    }

    @Override
    protected void setupMapper() {
        super.modelMapper.createTypeMap(Company.class, CompanyDTO.class)
                .addMappings(mapping -> mapping.skip(CompanyDTO::setUsersId)).setPostConverter(toDTOConverter());
        super.modelMapper.createTypeMap(CompanyDTO.class, Company.class)
                .addMappings(mapping -> mapping.skip(Company::setUsers)).setPostConverter(toEntityConverter());
    }

    @Override
    protected void mapSpecificFields(CompanyDTO source, Company destination) {
        if (!Objects.isNull(source.getUsersId())) {
            destination.setUsers(userRepository.findAllById(source.getUsersId()));
        }
        else {
            destination.setUsers(Collections.emptyList());
        }
    }

    @Override
    protected void mapSpecificFields(Company source, CompanyDTO destination) {
        destination.setUsersId(getIds(source));
    }


    @Override
    protected List<Long> getIds(Company entity) {
        return Objects.isNull(entity) || Objects.isNull(entity.getUsers())
                ? null
                : entity.getUsers().stream()
                .map(GenericModel::getId)
                .collect(Collectors.toList());
    }
}
