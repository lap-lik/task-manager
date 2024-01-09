package com.sber.finalsberproject.mapper;

import com.sber.finalsberproject.dto.TaskDTO;
import com.sber.finalsberproject.model.GenericModel;
import com.sber.finalsberproject.model.Task;
import com.sber.finalsberproject.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TaskMapper extends GenericMapper<Task, TaskDTO> {
    private final UserRepository userRepository;
    public TaskMapper(ModelMapper modelMapper,
                      UserRepository userRepository) {
        super(modelMapper, Task.class, TaskDTO.class);
        this.userRepository = userRepository;
    }
    @PostConstruct
    @Override
    protected void setupMapper() {
        modelMapper.createTypeMap(Task.class, TaskDTO.class)
                .addMappings(mapping -> mapping.skip(TaskDTO::setUsersIds)).setPostConverter(toDTOConverter());
        modelMapper.createTypeMap(TaskDTO.class, Task.class)
                .addMappings(mapping -> mapping.skip(Task::setUsers)).setPostConverter(toEntityConverter());
    }

    @Override
    protected void mapSpecificFields(TaskDTO source, Task destination) {
        if (!Objects.isNull(source.getUsersIds())) {
            destination.setUsers(userRepository.findAllById(source.getUsersIds()));
        } else {
            destination.setUsers(Collections.emptyList());
        }
    }

    @Override
    protected void mapSpecificFields(Task source, TaskDTO destination) {
        destination.setUsersIds(getIds(source));
    }

    @Override
    protected List<Long> getIds(Task source) {
        return Objects.isNull(source) || Objects.isNull(source.getUsers())
                ? Collections.emptyList()
                : source.getUsers().stream()
                .map(GenericModel::getId)
                .collect(Collectors.toList());
    }
}
