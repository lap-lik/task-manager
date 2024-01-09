package com.sber.finalsberproject.dto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommentDTO extends GenericDTO{
    private String text;
    private String onlineCopyPath;
    private String avatarOnlineCopyPath;
    private String fileName;
    private Long userId;
    private Long taskId;
}
