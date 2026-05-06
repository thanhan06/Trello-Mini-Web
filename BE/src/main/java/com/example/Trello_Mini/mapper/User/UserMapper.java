package com.example.Trello_Mini.mapper.User;

import com.example.Trello_Mini.dto.request.UserRequestCreation;
import com.example.Trello_Mini.dto.request.UserUpdationRequest;
import com.example.Trello_Mini.dto.response.UserResponse;
import com.example.Trello_Mini.entity.User.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userWorkspaces", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "role", constant = "USER")
    UserEntity toUserEntity(UserRequestCreation request);

    UserResponse toUserResponse(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "dob", ignore = true)
    @Mapping(target = "userWorkspaces", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    void updateUser(@MappingTarget UserEntity entity, UserUpdationRequest request);
}
