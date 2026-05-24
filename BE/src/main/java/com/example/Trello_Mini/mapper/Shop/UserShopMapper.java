package com.example.Trello_Mini.mapper.Shop;

import com.example.Trello_Mini.dto.request.Shop.UserCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.UserUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.UserResponse;
import com.example.Trello_Mini.entity.Shop.MstUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserShopMapper {

    @Mapping(target = "psnCd", ignore = true)
    @Mapping(target = "deleteTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    MstUserEntity toEntity(UserCreationRequest request);

    @Mapping(target = "psnCd", ignore = true)
    @Mapping(target = "deleteTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void update(@MappingTarget MstUserEntity entity, UserUpdateRequest request);

    @Mapping(target = "createPsnCd", source = "createdBy.psnCd")
    @Mapping(target = "updatePsnCd", source = "updatedBy.psnCd")
    UserResponse toResponse(MstUserEntity entity);
}
