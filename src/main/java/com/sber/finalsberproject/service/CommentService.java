package com.sber.finalsberproject.service;

import com.sber.finalsberproject.dto.CommentDTO;
import com.sber.finalsberproject.dto.SearchDTO;
import com.sber.finalsberproject.mapper.CommentMapper;
import com.sber.finalsberproject.model.Comment;
import com.sber.finalsberproject.repository.CommentRepository;
import com.sber.finalsberproject.utils.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
public class CommentService extends GenericService<Comment, CommentDTO> {
    public CommentService(CommentRepository commentRepository,
                          CommentMapper commentMapper) {
        super(commentRepository, commentMapper);
    }
    public CommentDTO create(final CommentDTO newComment, MultipartFile file) {
        String fileName = FileHelper.createFile(file);
        newComment.setOnlineCopyPath(fileName);
        return create(newComment);
    }

    public CommentDTO create(final CommentDTO newComment) {
        newComment.setUserId(newComment.getUserId());
        newComment.setTaskId(newComment.getTaskId());
        newComment.setText(newComment.getText());
        newComment.setCreatedWhen(LocalDateTime.now());
        newComment.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        return mapper.toDTO(repository.save(mapper.toEntity(newComment)));
    }

    public Page<CommentDTO> getAllCommentsByUserId(final Long id,
                                                final Pageable pageable) {
        Page<Comment> commentsPaginated = ((CommentRepository)repository).AllByUserId(id, pageable);
        List<CommentDTO> results = mapper.toDTOs(commentsPaginated.getContent());
        return new PageImpl<>(results, pageable, commentsPaginated.getTotalElements());
    }

    public List<CommentDTO> getAllCommentsByTaskId(final Long id) {
        List<Comment> commentsPaginated = ((CommentRepository)repository).AllByTaskId(id);
        return mapper.toDTOs(commentsPaginated);
    }

    public Page<CommentDTO> searchComment(SearchDTO searchDTO,
                                    Pageable pageRequest) {
        Page<Comment> commentsPaginated = ((CommentRepository) repository).searchCommentByTextOrAndCreatedWhen(
                searchDTO.getCommentText(),
                searchDTO.getCreatedWhen(),
                pageRequest
        );
        List<CommentDTO> result = mapper.toDTOs(commentsPaginated.getContent());
        return new PageImpl<>(result, pageRequest, commentsPaginated.getTotalElements());
    }
}
