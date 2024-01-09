package com.sber.finalsberproject.dto;

import com.sber.finalsberproject.model.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TaskDTO extends GenericDTO {
    private String taskTitle;
    private LocalDate fixPeriod;
    private Integer percentCurrent;
    private Integer percentDirective;
    private LocalDateTime completedWhen;
    private Long completedBy;
    private Long responsible;
    private boolean isCompleted;
    private Pattern pattern;
    private Long taskId;
    private List<Long> usersIds;
}
