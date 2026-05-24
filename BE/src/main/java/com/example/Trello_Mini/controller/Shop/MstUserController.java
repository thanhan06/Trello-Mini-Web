package com.example.Trello_Mini.controller.Shop;

import com.example.Trello_Mini.common.ApiResponse;
import com.example.Trello_Mini.common.ApiResponses;
import com.example.Trello_Mini.dto.request.Shop.UserCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.UserUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.UserResponse;
import com.example.Trello_Mini.service.Shop.MstUserService;
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
@RequestMapping("/shop/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MstUserController {

    MstUserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserCreationRequest request, HttpServletRequest httpReq) {
        return ApiResponses.created(httpReq, userService.create(request));
    }

    @PutMapping("/{psnCd}/update")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Integer psnCd, @Valid @RequestBody UserUpdateRequest request, HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, userService.update(psnCd, request));
    }

    @GetMapping("/{psnCd}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Integer psnCd, HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, userService.getById(psnCd));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> list(HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, userService.list());
    }

    @DeleteMapping("/{psnCd}/delete")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Integer psnCd, HttpServletRequest httpReq) {
        userService.delete(psnCd);
        return ApiResponses.ok(httpReq, null);
    }
}
