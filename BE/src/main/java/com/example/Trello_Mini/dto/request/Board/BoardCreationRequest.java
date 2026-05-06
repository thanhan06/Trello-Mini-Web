package com.example.Trello_Mini.dto.request.Board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BoardCreationRequest {

    @NotBlank(message = "BOARD_NAME_REQUIRED")
    String name;

    String description;

    @NotNull(message = "WORKSPACE_ID_REQUIRED")
    Long workspaceId;
}

