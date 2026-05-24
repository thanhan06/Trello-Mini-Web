package com.example.Trello_Mini.controller.Shop;

import com.example.Trello_Mini.common.ApiResponse;
import com.example.Trello_Mini.common.ApiResponses;
import com.example.Trello_Mini.dto.request.Shop.ProductCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.ProductUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.ProductResponse;
import com.example.Trello_Mini.service.Shop.ProductService;
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
@RequestMapping("/shop/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductCreationRequest request, HttpServletRequest httpReq) {
        return ApiResponses.created(httpReq, productService.create(request));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request, HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, productService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id, HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, productService.getById(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> list(HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, productService.list());
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id, HttpServletRequest httpReq) {
        productService.delete(id);
        return ApiResponses.ok(httpReq, null);
    }
}
