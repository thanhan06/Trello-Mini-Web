package com.example.Trello_Mini.controller.Shop;

import com.example.Trello_Mini.common.ApiResponse;
import com.example.Trello_Mini.common.ApiResponses;
import com.example.Trello_Mini.dto.request.Shop.OrderCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.OrderUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.OrderResponse;
import com.example.Trello_Mini.service.Shop.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody OrderCreationRequest request, HttpServletRequest httpReq) {
        return ApiResponses.created(httpReq, orderService.create(request));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse<OrderResponse>> update(
            @PathVariable Long id, @Valid @RequestBody OrderUpdateRequest request, HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, orderService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable Long id, HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, orderService.getById(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> list(HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, orderService.list());
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id, HttpServletRequest httpReq) {
        orderService.delete(id);
        return ApiResponses.ok(httpReq, null);
    }
}
