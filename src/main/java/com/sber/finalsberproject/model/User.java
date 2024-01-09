package com.sber.finalsberproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users",
        uniqueConstraints = {@UniqueConstraint(name = "uniqueEmail", columnNames = "email"),
                @UniqueConstraint(name = "uniqueLogin", columnNames = "login"),
                @UniqueConstraint(name = "uniqueMobilePhone", columnNames = "mobile_phone")})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "default_generator", sequenceName = "user_seq", allocationSize = 1)
public class User extends GenericModel {

    @Column(name = "online_copy_path")
    private String onlineCopyPath;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @Column(name = "mobile_phone", nullable = false)
    private String mobilePhone;


    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "is_activated", columnDefinition = "boolean default false")
    private Boolean isActivated;

    @Column(name = "activated_before", nullable = false)
    private LocalDate activatedBefore;

    @ManyToOne
    @JoinColumn(name = "company_id",
            foreignKey = @ForeignKey(name = "FK_COMPANY_USER"))
    private Company company;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "role_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_USERS_ROLES"))
    private Role role;

    @ManyToMany
    @JoinTable(name = "users_tasks",
            joinColumns = @JoinColumn(name = "user_id"), foreignKey = @ForeignKey(name = "FK_USERS_TASKS"),
            inverseJoinColumns = @JoinColumn(name = "task_id"), inverseForeignKey = @ForeignKey(name = "FK_TASKS_USERS"))
    private List<Task> tasks;
}
