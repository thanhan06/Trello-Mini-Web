package com.example.Trello_Mini.service.Shop;

import com.example.Trello_Mini.dto.request.Shop.UserCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.UserUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.UserResponse;
import java.util.List;

public interface MstUserService {
    UserResponse create(UserCreationRequest request);

    UserResponse update(Integer psnCd, UserUpdateRequest request);

    UserResponse getById(Integer psnCd);

    UserResponse getByUsername(String username);

    List<UserResponse> list();

    void delete(Integer psnCd);
}
