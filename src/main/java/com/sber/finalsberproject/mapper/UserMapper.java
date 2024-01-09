package com.sber.finalsberproject.mapper;

import com.sber.finalsberproject.dto.UserDTO;
import com.sber.finalsberproject.model.GenericModel;
import com.sber.finalsberproject.model.User;
import com.sber.finalsberproject.repository.CompanyRepository;
import com.sber.finalsberproject.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserMapper extends GenericMapper<User, UserDTO> {
    private final TaskRepository taskRepository;
    private final CompanyRepository companyRepository;

    public UserMapper(ModelMapper modelMapper,
                      TaskRepository taskRepository,
                      CompanyRepository companyRepository) {
        super(modelMapper, User.class, UserDTO.class);
        this.taskRepository = taskRepository;
        this.companyRepository = companyRepository;
    }

    @PostConstruct
    @Override
    protected void setupMapper() {
        modelMapper.createTypeMap(User.class, UserDTO.class)
                .addMappings(mapping -> {
                    mapping.skip(UserDTO::setTasksIds);
                    mapping.skip(UserDTO::setCompanyId);
                })
                .setPostConverter(toDTOConverter());
        modelMapper.createTypeMap(UserDTO.class, User.class)
                .addMappings(mapping -> {
                    mapping.skip(User::setTasks);
                    mapping.skip(User::setCompany);
                })
                .setPostConverter(toEntityConverter());
    }
    @Override
    protected void mapSpecificFields(UserDTO source, User destination) {
        if (!Objects.isNull(source.getTasksIds())) {
            destination.setTasks(taskRepository.findAllById(source.getTasksIds()));

        }
        if (!Objects.isNull(source.getCompanyId())) {
            destination.setCompany(companyRepository.findById(source.getCompanyId()).orElseThrow(() -> new NotFoundException("Фирма не найдена")));
        }
        destination.setTasks(Collections.emptyList());
    }
    @Override
    protected void mapSpecificFields(User source, UserDTO destination) {
        destination.setTasksIds(getIds(source));
        if (!Objects.isNull(source.getCompany())) {
            destination.setCompanyId(source.getCompany().getId());
        }
    }
    @Override
    protected List<Long> getIds(User source) {
        return Objects.isNull(source) || Objects.isNull(source.getTasks())
                ? Collections.emptyList()
                : source.getTasks().stream()
                .map(GenericModel::getId)
                .collect(Collectors.toList());
    }
}
