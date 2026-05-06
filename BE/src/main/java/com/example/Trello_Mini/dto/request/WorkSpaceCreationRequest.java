package com.example.Trello_Mini.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class WorkSpaceCreationRequest {

    @NotBlank(message = "WORKSPACE_NAME_REQUIRED")
    String name;

    String description;
}
