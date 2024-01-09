package com.sber.finalsberproject.mapper;

import com.sber.finalsberproject.dto.CommentDTO;
import com.sber.finalsberproject.model.Comment;

import com.sber.finalsberproject.repository.TaskRepository;
import com.sber.finalsberproject.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;

import java.util.List;

@Component
public class CommentMapper extends GenericMapper<Comment, CommentDTO> {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    public CommentMapper(ModelMapper modelMapper,
                         UserRepository userRepository,
                         TaskRepository taskRepository) {
        super(modelMapper, Comment.class, CommentDTO.class);
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    protected void setupMapper() {
        super.modelMapper.createTypeMap(Comment.class, CommentDTO.class)
                .addMappings(m -> m.skip(CommentDTO::setUserId))
                .addMappings(m -> m.skip(CommentDTO::setTaskId))
                .setPostConverter(toDTOConverter());

        super.modelMapper.createTypeMap(CommentDTO.class, Comment.class)
                .addMappings(m -> m.skip(Comment::setUser))
                .addMappings(m -> m.skip(Comment::setTask))
                .setPostConverter(toEntityConverter());
    }
    @Override
    protected void mapSpecificFields(CommentDTO source, Comment destination) {
        destination.setUser(userRepository.findById(source.getUserId()).orElseThrow(() -> new NotFoundException("Пользователь не найден")));
        destination.setTask(taskRepository.findById(source.getTaskId()).orElseThrow(() -> new NotFoundException("Задача не найдена")));
    }

    @Override
    protected void mapSpecificFields(Comment source, CommentDTO destination) {
        destination.setUserId(source.getUser().getId());
        destination.setTaskId(source.getTask().getId());
    }

    @Override
    protected List<Long> getIds(Comment entity) {
        throw new UnsupportedOperationException("Метод недоступен");
    }
}
