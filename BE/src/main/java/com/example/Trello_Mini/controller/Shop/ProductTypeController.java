package com.example.Trello_Mini.controller.Shop;

import com.example.Trello_Mini.common.ApiResponse;
import com.example.Trello_Mini.common.ApiResponses;
import com.example.Trello_Mini.dto.request.Shop.ProductTypeCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.ProductTypeUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.ProductTypeResponse;
import com.example.Trello_Mini.service.Shop.ProductTypeService;
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
@RequestMapping("/shop/product-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductTypeController {

    ProductTypeService productTypeService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductTypeResponse>> create(@Valid @RequestBody ProductTypeCreationRequest request, HttpServletRequest httpReq) {
        return ApiResponses.created(httpReq, productTypeService.create(request));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse<ProductTypeResponse>> update(
            @PathVariable Integer id, @Valid @RequestBody ProductTypeUpdateRequest request, HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, productTypeService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductTypeResponse>> getById(@PathVariable Integer id, HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, productTypeService.getById(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductTypeResponse>>> list(HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, productTypeService.list());
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Integer id, HttpServletRequest httpReq) {
        productTypeService.delete(id);
        return ApiResponses.ok(httpReq, null);
    }
}
