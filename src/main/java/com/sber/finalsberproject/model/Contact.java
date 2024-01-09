package com.sber.finalsberproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contacts",
        uniqueConstraints = {@UniqueConstraint(name = "uniqueEmail", columnNames = "email"),
                @UniqueConstraint(name = "uniqueMobilePhone", columnNames = "mobile_phone")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "default_generator", sequenceName = "contacts_seq", allocationSize = 1)

public class Contact extends GenericModel {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "mobile_phone", nullable = false)
    private String mobilePhone;

    @Column(name = "company_title")
    private String companyTitle;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "position")
    private String position;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_USER_CONTACTS"))
    private User user;
}
