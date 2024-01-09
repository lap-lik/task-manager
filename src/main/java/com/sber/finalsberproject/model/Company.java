package com.sber.finalsberproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "companys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "default_generator", sequenceName = "comment_seq", allocationSize = 1)
public class Company extends GenericModel{

    private String title;

    @OneToMany(mappedBy = "company")
    private List<User> users;
}
