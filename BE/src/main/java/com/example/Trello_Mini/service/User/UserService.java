package com.example.Trello_Mini.service.User;

import com.example.Trello_Mini.common.ApiException;
import com.example.Trello_Mini.common.ErrorCode;
import com.example.Trello_Mini.dto.request.UserRequestCreation;
import com.example.Trello_Mini.dto.response.UserResponse;
import com.example.Trello_Mini.entity.User.UserEntity;
import com.example.Trello_Mini.repository.User.UserRepository;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequestCreation userRequestCreation) {
        // Check if email already exists
        if (userRepository.existsByEmail(userRequestCreation.getEmail())) {
            throw new ApiException(ErrorCode.USER_EMAIL_EXISTS);
        }

        // Create new UserEntity from UserRequestCreation
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userRequestCreation.getName());
        userEntity.setEmail(userRequestCreation.getEmail());
        userEntity.setPassword(passwordEncoder.encode(userRequestCreation.getPassword())); // Hash the password before saving
        userEntity.setRole(userRequestCreation.getRole());
        userEntity.setDob(userRequestCreation.getDob());
        // Save the new user to the database
        UserEntity savedUser = userRepository.save(userEntity);

        // Convert saved UserEntity to UserResponse and return
        return new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole(), savedUser.getDob());
    }

}
