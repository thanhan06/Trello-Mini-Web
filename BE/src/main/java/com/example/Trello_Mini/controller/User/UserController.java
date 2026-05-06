package com.example.Trello_Mini.controller.User;

import com.example.Trello_Mini.common.ApiResponse;
import com.example.Trello_Mini.common.ApiResponses;
import com.example.Trello_Mini.dto.request.UserRequestCreation;
import com.example.Trello_Mini.dto.response.UserResponse;
import com.example.Trello_Mini.service.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)

public class UserController {
    UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRequestCreation userRequestCreation, HttpServletRequest httpReq) {
        return ApiResponses.created(httpReq, userService.createUser(userRequestCreation));
    }

    @PreAuthorize("hasRole('ADMIN') or @userService.getUserById(#id).email == authentication.name")
    @GetMapping ("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id, HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, userService.getUserById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(HttpServletRequest httpReq) {
        String email = httpReq.getUserPrincipal().getName();
        return ApiResponses.ok(httpReq, userService.getMyInfo(email));
    }

}
