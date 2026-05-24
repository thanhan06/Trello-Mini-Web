package com.example.Trello_Mini.service.Shop;

import com.example.Trello_Mini.dto.request.Shop.ProductTypeCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.ProductTypeUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.ProductTypeResponse;
import java.util.List;

public interface ProductTypeService {
    ProductTypeResponse create(ProductTypeCreationRequest request);

    ProductTypeResponse update(Integer id, ProductTypeUpdateRequest request);

    ProductTypeResponse getById(Integer id);

    List<ProductTypeResponse> list();

    void delete(Integer id);
}
