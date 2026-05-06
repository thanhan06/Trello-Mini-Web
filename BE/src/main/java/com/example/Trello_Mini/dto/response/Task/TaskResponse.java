package com.example.Trello_Mini.dto.response.Task;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskResponse {
    Long id;
    String title;
    String description;
    String status;
    Long boardId;
    UUID assignedToId;
}
