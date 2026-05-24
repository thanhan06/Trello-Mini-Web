package com.example.Trello_Mini.mapper.Shop;

import com.example.Trello_Mini.dto.response.Shop.OrderResponse;
import com.example.Trello_Mini.entity.Shop.TrProductOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderProductId", source = "product.productId")
    @Mapping(target = "orderProductName", source = "product.productName")
    @Mapping(target = "createUser", source = "createdBy.psnCd")
    @Mapping(target = "updateUser", source = "updatedBy.psnCd")
    OrderResponse toResponse(TrProductOrderEntity entity);
}
