package com.example.autoposting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SavePostRequest {

    private String message;

    private String url;

    private int[] profiles;

    private String[] postType;
}
