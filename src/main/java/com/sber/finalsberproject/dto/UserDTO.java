package com.sber.finalsberproject.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO extends GenericDTO {
    private String login;
    private String password;
    private String email;
    private LocalDate birthDate;
    private String firstName;
    private String lastName;
    private String middleName;
    private String mobilePhone;
    private String onlineCopyPath;
    private String address;
    private Boolean isActivated;
    private LocalDate activatedBefore;
    private RoleDTO role;
    private List<Long> tasksIds;
    private Long companyId;
}
