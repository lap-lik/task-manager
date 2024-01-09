package com.sber.finalsberproject.controllers;

import com.sber.finalsberproject.dto.CommentDTO;
import com.sber.finalsberproject.service.CommentService;
import com.sber.finalsberproject.service.userdetails.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/comments")
public class MVCCommentController {
    private final CommentService commentService;

    public MVCCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    private CustomUserDetails myCustomUserDetails (){
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    @GetMapping("/add-by-big")
    public String createByBigTask() {
        return "view-all";
    }

    @PostMapping("/add-by-big")
    public String createByBigTask(@ModelAttribute("commentForm") CommentDTO newComment,
                         @RequestParam(value = "onlineCopy") MultipartFile file) {
        CustomUserDetails customUserDetails = myCustomUserDetails ();
        newComment.setUserId(Long.valueOf(myCustomUserDetails().getUserId()));
        newComment.setCreatedBy(customUserDetails.getUsername());
        if (file != null && file.getSize() > 0) {
            commentService.create(newComment, file);
        } else {
            commentService.create(newComment);
        }
        return "redirect:/tasks/big/" + newComment.getTaskId();
    }

    @GetMapping("/add-by-medium")
    public String createByMediumTask() {
        return "/tasks/medium/view-all";
    }

    @PostMapping("/add-by-medium")
    public String createByMediumTask(@ModelAttribute("commentForm") CommentDTO newComment,
                                  @RequestParam(value = "onlineCopy") MultipartFile file) {
        CustomUserDetails customUserDetails = myCustomUserDetails ();
        newComment.setUserId(Long.valueOf(myCustomUserDetails().getUserId()));
        newComment.setCreatedBy(customUserDetails.getUsername());
        if (file != null && file.getSize() > 0) {
            commentService.create(newComment, file);
        } else {
            commentService.create(newComment);
        }
        return "redirect:/tasks/medium/" + newComment.getTaskId();
    }

    @GetMapping(value = "/")
    public String getAllByUserId(@RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int pageSize,
                                 @ModelAttribute(name = "exception") final String exception,
                                 Model model) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "created_when"));
        Page<CommentDTO> result;
        result = commentService.getAllCommentsByUserId(Long.valueOf(myCustomUserDetails().getUserId()), pageRequest);
        model.addAttribute("comments", result);
        return "/tasks/medium/view-all";
    }

    @GetMapping(value = "/{id}")
    public String getAllByTaskId(@ModelAttribute(name = "exception") final String exception,
                                 @PathVariable Long id,
                                 Model model) {
        List<CommentDTO> result = commentService.getAllCommentsByTaskId(id);
        model.addAttribute("comments", result);
        return "/tasks/medium/view-all";
    }


    @GetMapping(value = "/download", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@Param(value = "commentId") Long commentId) throws IOException {
        CommentDTO commentDTO = commentService.getOne(commentId);
        Path path = Paths.get(commentDTO.getOnlineCopyPath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(createHeaders(path.getFileName().toString()))
                .contentLength(path.toFile().length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private HttpHeaders createHeaders(final String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);
        headers.add("Cache-Control", "no-cache, no-store");
        headers.add("Expires", "0");
        return headers;
    }
}
