package com.sber.finalsberproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "default_generator", sequenceName = "task_seq", allocationSize = 1)
public class Task extends GenericModel {

    @Column(name = "task_title", nullable = false)
    private String taskTitle;

    @Column(name = "pattern", nullable = false)
    @Enumerated
    private Pattern pattern;

    @Column(name = "percent_current")
    private Integer percentCurrent;

    @Column(name = "percent_directive")
    private Integer percentDirective;

    @Column(name = "fix_period")
    private LocalDate fixPeriod;

    @Column(name = "completed_by")
    private Long completedBy;

    @Column(name = "completed_when")
    private LocalDateTime completedWhen;

    @Column(name = "is_completed", columnDefinition = "boolean default false")
    private boolean isCompleted;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "responsible")
    private Long responsible;

    @ManyToMany
    @JoinTable(name = "users_tasks",
            joinColumns = @JoinColumn(name = "task_id"), foreignKey = @ForeignKey(name = "FK_TASKS_USERS"),
            inverseJoinColumns = @JoinColumn(name = "user_id"), inverseForeignKey = @ForeignKey(name = "FK_USERS_TASKS"))
    private List<User> users;
}
