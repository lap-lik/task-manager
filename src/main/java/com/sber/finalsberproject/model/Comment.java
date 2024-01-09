package com.sber.finalsberproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "default_generator", sequenceName = "comment_seq", allocationSize = 1)
public class Comment extends GenericModel {

    @Column(name = "text")
    private String text;

    @Column(name = "update_when")
    private LocalDateTime updateWhen;

    @Column(name = "online_copy_path")
    private String onlineCopyPath;

    @Column(name = "avatar_online_copy_path")
    private String avatarOnlineCopyPath;

    @Column(name = "file_name")
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_USER_COMMENTS"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_TASK_COMMENTS"))
    private Task task;
}
