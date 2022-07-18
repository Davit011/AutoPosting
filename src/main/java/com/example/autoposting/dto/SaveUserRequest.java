package com.example.autoposting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SaveUserRequest {

    private int id;

    private String name;

    private String token;

    private String profileId;

    private String userType;

}
