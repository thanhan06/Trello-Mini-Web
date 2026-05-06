package com.example.Trello_Mini.mapper.WorkSpace;

import com.example.Trello_Mini.dto.request.WorkSpaceCreationRequest;
import com.example.Trello_Mini.dto.response.WorkSpaceResponse;
import com.example.Trello_Mini.entity.WorkSpace.WorkSpaceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkSpaceMapper {

    WorkSpaceEntity toWorkSpaceEntity(WorkSpaceCreationRequest request);

    WorkSpaceResponse toWorkSpaceResponse(WorkSpaceEntity entity);
}
