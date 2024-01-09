package com.sber.finalsberproject.dto;
import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CompanyDTO extends GenericDTO{
    private String title;
    private List<Long> usersId;
}
