package com.example.Trello_Mini.service.User;
import com.example.Trello_Mini.dto.request.UserRequestCreation;
import com.example.Trello_Mini.dto.response.UserResponse;
public interface UserService {
    UserResponse createUser(UserRequestCreation userRequestCreation);
    UserResponse getUserById(String id);
    UserResponse getMyInfo(String email);
}
