package com.sber.finalsberproject.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AddUserDTO {
    Long userId;
    Long companyId;
    Long taskId;
}
