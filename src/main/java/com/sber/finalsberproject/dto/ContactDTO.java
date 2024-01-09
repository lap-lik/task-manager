package com.sber.finalsberproject.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ContactDTO extends GenericDTO {
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String mobilePhone;
    private String companyTitle;
    private String companyAddress;
    private String position;
    private String description;
    private Long userId;
}
