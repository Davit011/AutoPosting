package com.example.autoposting.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String profileId;

    private String instagramId;

    private String name;

    private String token;

    @Enumerated(EnumType.STRING)
    private UserType profileType;

    @Enumerated(EnumType.STRING)
    private UserCategory category;

    private boolean isChecked;

    private UserStatus status;
}
