package com.example.Trello_Mini.controller.User;
import com.example.Trello_Mini.common.ApiResponse;
import com.example.Trello_Mini.common.ApiResponses;
import com.example.Trello_Mini.dto.request.AuthenticationRequest;
import com.example.Trello_Mini.dto.response.AuthenticationResponse;
import com.example.Trello_Mini.service.User.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @RequestBody AuthenticationRequest request,
            HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, authenticationService.authenticate(request));
    }
}
