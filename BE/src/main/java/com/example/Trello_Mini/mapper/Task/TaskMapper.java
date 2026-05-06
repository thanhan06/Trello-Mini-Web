package com.example.Trello_Mini.mapper.Task;

import com.example.Trello_Mini.dto.request.Task.TaskCreationRequest;
import com.example.Trello_Mini.dto.request.Task.TaskUpdateRequest;
import com.example.Trello_Mini.dto.response.Task.TaskResponse;
import com.example.Trello_Mini.entity.Task.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "board", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    TaskEntity toTaskEntity(TaskCreationRequest request);

    @Mapping(target = "boardId", source = "board.id")
    @Mapping(target = "assignedToId", source = "assignedTo.id")
    TaskResponse toTaskResponse(TaskEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "board", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    void updateTaskEntity(@MappingTarget TaskEntity entity, TaskUpdateRequest request);
}
