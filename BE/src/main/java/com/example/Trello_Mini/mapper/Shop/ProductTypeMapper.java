package com.example.Trello_Mini.mapper.Shop;

import com.example.Trello_Mini.dto.request.Shop.ProductTypeCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.ProductTypeUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.ProductTypeResponse;
import com.example.Trello_Mini.entity.Shop.MstProductTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductTypeMapper {

    @Mapping(target = "producttypeId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    MstProductTypeEntity toEntity(ProductTypeCreationRequest request);

    @Mapping(target = "producttypeId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void update(@MappingTarget MstProductTypeEntity entity, ProductTypeUpdateRequest request);

    @Mapping(target = "createUser", source = "createdBy.psnCd")
    @Mapping(target = "updateUser", source = "updatedBy.psnCd")
    ProductTypeResponse toResponse(MstProductTypeEntity entity);
}
