package com.example.Trello_Mini.service.Shop;

import com.example.Trello_Mini.dto.request.Shop.ProductCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.ProductUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.ProductResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ProductService {
    ProductResponse create(ProductCreationRequest request);

    ProductResponse update(Long id, ProductUpdateRequest request);

    ProductResponse getById(Long id);

    List<ProductResponse> list();

    Page<ProductResponse> getProducts(int pageNo, int pageSize,
                                          String name, String type, String desc);

    void delete(Long id);
}
