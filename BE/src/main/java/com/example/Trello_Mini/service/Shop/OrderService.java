package com.example.Trello_Mini.service.Shop;

import com.example.Trello_Mini.dto.request.Shop.OrderCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.OrderUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse create(OrderCreationRequest request);

    OrderResponse update(Long id, OrderUpdateRequest request);

    OrderResponse getById(Long id);

    List<OrderResponse> list();

    void delete(Long id);
}
