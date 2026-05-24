package com.example.Trello_Mini.mapper.Shop;

import com.example.Trello_Mini.dto.request.Shop.ProductCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.ProductUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.ProductResponse;
import com.example.Trello_Mini.entity.Shop.MstProductEntity;
import com.example.Trello_Mini.entity.Shop.MstProductTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "productType", source = "productType")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    MstProductEntity toEntity(ProductCreationRequest request, MstProductTypeEntity productType);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "productType", source = "productType")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void update(@MappingTarget MstProductEntity entity, ProductUpdateRequest request, MstProductTypeEntity productType);

    @Mapping(target = "producttypeId", source = "productType.producttypeId")
    @Mapping(target = "producttypeName", source = "productType.name")
    @Mapping(target = "createUser", source = "createdBy.psnCd")
    @Mapping(target = "updateUser", source = "updatedBy.psnCd")
    ProductResponse toResponse(MstProductEntity entity);
}
