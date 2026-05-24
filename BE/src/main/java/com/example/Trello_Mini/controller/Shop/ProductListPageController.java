package com.example.Trello_Mini.controller.Shop;

import com.example.Trello_Mini.service.Shop.ProductService;
import com.example.Trello_Mini.service.Shop.ProductTypeService;
import com.example.Trello_Mini.dto.response.Shop.ProductResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.Trello_Mini.service.Shop.MstUserService;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductListPageController {

    ProductService productService;
    ProductTypeService productTypeService;
    MstUserService mstUserService;

    @GetMapping({"/shop/product-list", "/productlist"})
    public String productList(
            Model model,
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "5")  int size,
            @RequestParam(defaultValue = "")   String name,
            @RequestParam(defaultValue = "")   String type,
            @RequestParam(defaultValue = "")   String desc,
            @AuthenticationPrincipal UserDetails userDetails) {

        Page<ProductResponse> productPage = productService.getProducts(page, size, name, type, desc);

        model.addAttribute("products",    productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",  productPage.getTotalPages());
        model.addAttribute("totalItems",  productPage.getTotalElements());
        model.addAttribute("pageSize",    size);
        model.addAttribute("productTypes", productTypeService.list());

        // Giữ lại điều kiện search để pagination dùng
        model.addAttribute("searchName", name);
        model.addAttribute("searchType", type);
        model.addAttribute("searchDesc", desc);

        if (userDetails != null) {
            String username = userDetails.getUsername();
            try {
                var user = mstUserService.getByUsername(username);
                model.addAttribute("username",    user.getUsername());
                model.addAttribute("displayName", user.getUserId()); // Sửa lại thành lấy một trường khác hợp lý (VD: UserId hoặc tên đầy đủ)
            } catch (Exception e) {
                model.addAttribute("username",    username);
                model.addAttribute("displayName", username);
            }
        } else {
            model.addAttribute("username",    "");
            model.addAttribute("displayName", "");
        }

        return "listproduct";
    }
}