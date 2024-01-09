package com.sber.finalsberproject.service;

import com.sber.finalsberproject.dto.SearchDTO;
import com.sber.finalsberproject.dto.TaskDTO;
import com.sber.finalsberproject.mapper.TaskMapper;
import com.sber.finalsberproject.model.Task;
import com.sber.finalsberproject.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TaskService extends GenericService<Task, TaskDTO>{

    public TaskService(TaskRepository taskRepository,
                       TaskMapper taskMapper) {
        super(taskRepository, taskMapper);
    }
    public TaskDTO create(final TaskDTO newTask) {
        newTask.setCreatedWhen(LocalDateTime.now());
        newTask.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        return mapper.toDTO(repository.save(mapper.toEntity(newTask)));
    }

    public Page<TaskDTO> listAllByPattern( final Pageable pageable,
                                        String pattern) {
        if (pattern.equals("big")){
            Page<Task> tasksPaginated = ((TaskRepository)repository).allTasksByPattern(pageable, 0);
            List<TaskDTO> results = mapper.toDTOs(tasksPaginated.getContent());
            return new PageImpl<>(results, pageable, tasksPaginated.getTotalElements());
        } else if (pattern.equals("medium")) {
            Page<Task> tasksPaginated = ((TaskRepository)repository).allTasksByPattern(pageable, 1);
            List<TaskDTO> results = mapper.toDTOs(tasksPaginated.getContent());
            return new PageImpl<>(results, pageable, tasksPaginated.getTotalElements());
        }else {
            Page<Task> tasksPaginated = ((TaskRepository)repository).allTasksByPattern(pageable, 2);
            List<TaskDTO> results = mapper.toDTOs(tasksPaginated.getContent());
            return new PageImpl<>(results, pageable, tasksPaginated.getTotalElements());
        }
    }
    public List<TaskDTO> listAllByPatternForMain(Integer pattern) {
        List<Task> tasks = ((TaskRepository)repository).allTasksByPatternForMian(pattern);
        return mapper.toDTOs(tasks);
    }
    public List<TaskDTO> getListByUserIdForMain(final Long id,
                                         Integer pattern) {
        List<Task> tasks = ((TaskRepository)repository).listTasksByUserByPatternForMain(id, pattern);
        return mapper.toDTOs(tasks);
    }

    public Page<TaskDTO> getAllByUserId(final Long id,
                                        final Pageable pageable,
                                        String pattern) {
        if (pattern.equals("big")){
            Page<Task> tasksPaginated = ((TaskRepository)repository).allTasksByUserByPattern(id, pageable, 0);
            List<TaskDTO> results = mapper.toDTOs(tasksPaginated.getContent());
            return new PageImpl<>(results, pageable, tasksPaginated.getTotalElements());
        } else if (pattern.equals("medium")) {
            Page<Task> tasksPaginated = ((TaskRepository)repository).allTasksByUserByPattern(id, pageable, 1);
            List<TaskDTO> results = mapper.toDTOs(tasksPaginated.getContent());
            return new PageImpl<>(results, pageable, tasksPaginated.getTotalElements());
        }else {
            Page<Task> tasksPaginated = ((TaskRepository)repository).allTasksByUserByPattern(id, pageable, 2);
            List<TaskDTO> results = mapper.toDTOs(tasksPaginated.getContent());
            return new PageImpl<>(results, pageable, tasksPaginated.getTotalElements());
        }
    }
    public void addTaskToUser(final Long taskId, final Long userId) {
        TaskDTO taskDTO = getOne(taskId);
        taskDTO.getUsersIds().add(userId);
        update(taskDTO);
    }


    public void removeUserFromTask(final Long taskId, final Long userId) {
        TaskDTO taskDTO = getOne(taskId);
        taskDTO.getUsersIds().remove(userId);
        update(taskDTO);
    }

    public TaskDTO closed(final Long taskId, final Long userId) {
        TaskDTO taskDTO = getOne(taskId);
        taskDTO.setCompleted(true);
        taskDTO.setCompletedBy(userId);
        taskDTO.setCompletedWhen(LocalDateTime.now());
        update(taskDTO);
        return taskDTO;
    }

    public void disclosure(final Long taskId, final Long userId) {
        TaskDTO taskDTO = getOne(taskId);
        taskDTO.setCompleted(false);
        taskDTO.setCompletedBy(null);
        taskDTO.setCompletedWhen(null);
        update(taskDTO);
    }

    public void addResponsible(final Long taskId, final Long userId) {
        TaskDTO taskDTO = getOne(taskId);
        taskDTO.setResponsible(userId);
        update(taskDTO);
    }

    public void removeResponsible(final Long taskId) {
        TaskDTO taskDTO = getOne(taskId);
        taskDTO.setResponsible(null);
        update(taskDTO);
    }

    public TaskDTO getLastTaskIdByUser (final Long id){
        return mapper.toDTO(((TaskRepository) repository).findLastTaskByUserId(id));
    }

    public Page<TaskDTO> getAllTasksByUserId(final Long id,
                                             final Pageable pageable) {
        Page<Task> tasksPaginated = ((TaskRepository)repository).AllTasksByUser(id, pageable);
        List<TaskDTO> results = mapper.toDTOs(tasksPaginated.getContent());
        return new PageImpl<>(results, pageable, tasksPaginated.getTotalElements());
    }
    public Page<TaskDTO> getAllBigTasksByUserId(final Long id,
                                             final Pageable pageable) {
        Page<Task> tasksPaginated = ((TaskRepository)repository).AllBigTasksByUser(id, pageable);
        List<TaskDTO> results = mapper.toDTOs(tasksPaginated.getContent());
        return new PageImpl<>(results, pageable, tasksPaginated.getTotalElements());
    }
    public List<TaskDTO> getAllTasksByTaskId(final Long id) {
        List<Task> tasks = ((TaskRepository)repository).findAllByTaskId(id);
        return mapper.toDTOs(tasks);
    }
    public List<TaskDTO> getAllBigTasksByUserId(final Long id) {
        List<Task> tasks = ((TaskRepository)repository).AllBigTasksByUser(id);
        return mapper.toDTOs(tasks);
    }
    public Page<TaskDTO> getAllMediumTasksByUserId(final Long id,
                                                final Pageable pageable) {
        Page<Task> tasksPaginated = ((TaskRepository)repository).AllMediumTasksByUser(id, pageable);
        List<TaskDTO> results = mapper.toDTOs(tasksPaginated.getContent());
        return new PageImpl<>(results, pageable, tasksPaginated.getTotalElements());
    }
    public List<TaskDTO> getAllSmallTasksByUserId(final Long id) {
        List<Task> tasks = ((TaskRepository)repository).AllSmallTasksByUser(id);
        return mapper.toDTOs(tasks);
    }
    public Page<TaskDTO> getAllSmallTasksByUserId(final Long id,
                                                   final Pageable pageable) {
        Page<Task> tasksPaginated = ((TaskRepository)repository).AllSmallTasksByUser(id, pageable);
        List<TaskDTO> results = mapper.toDTOs(tasksPaginated.getContent());
        return new PageImpl<>(results, pageable, tasksPaginated.getTotalElements());
    }
    public Page<TaskDTO> searchTask(SearchDTO searchDTO,
                                      Pageable pageRequest) {
        Page<Task> tasksPaginated = ((TaskRepository) repository).searchTasks(
                searchDTO.getTaskTitle(),
                searchDTO.getCreatedWhen(),
                searchDTO.getCompletedWhen(),
                pageRequest
        );
        List<TaskDTO> result = mapper.toDTOs(tasksPaginated.getContent());
        return new PageImpl<>(result, pageRequest, tasksPaginated.getTotalElements());
    }
}
