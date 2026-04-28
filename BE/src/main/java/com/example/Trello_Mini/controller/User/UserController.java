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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
