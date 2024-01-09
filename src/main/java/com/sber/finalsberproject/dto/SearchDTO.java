package com.sber.finalsberproject.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
@Getter
@Setter
@ToString
public class SearchDTO {
    private String taskTitle;
    private String commentText;
    private LocalDateTime createdWhen;
    private LocalDateTime completedWhen;
}
