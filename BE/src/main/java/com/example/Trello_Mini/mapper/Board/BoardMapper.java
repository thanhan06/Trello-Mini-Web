package com.example.Trello_Mini.mapper.Board;

import com.example.Trello_Mini.dto.request.Board.BoardCreationRequest;
import com.example.Trello_Mini.dto.response.Board.BoardResponse;
import com.example.Trello_Mini.entity.Board.BoardEntity;
import com.example.Trello_Mini.entity.WorkSpace.WorkSpaceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BoardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workspace", source = "workspace")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "tasks", ignore = true)
    BoardEntity toBoardEntity(BoardCreationRequest request, WorkSpaceEntity workspace);

    @Mapping(target = "workspaceId", source = "workspace.id")
    BoardResponse toBoardResponse(BoardEntity entity);
}
