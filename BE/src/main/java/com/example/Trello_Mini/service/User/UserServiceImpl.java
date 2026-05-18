package com.example.Trello_Mini.service.User;

import com.example.Trello_Mini.common.ApiException;
import com.example.Trello_Mini.common.ErrorCode;
import com.example.Trello_Mini.dto.request.UserRequestCreation;
import com.example.Trello_Mini.dto.response.UserResponse;
import com.example.Trello_Mini.entity.User.UserEntity;
import com.example.Trello_Mini.mapper.User.UserMapper;
import com.example.Trello_Mini.repository.User.UserRepository;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserResponse createUser(UserRequestCreation userRequestCreation) {
        // Check if email already exists
        if (userRepository.existsByEmail(userRequestCreation.getEmail())) {
            throw new ApiException(ErrorCode.USER_EMAIL_EXISTS);
        }

        // Create new UserEntity from UserRequestCreation
        UserEntity userEntity = userMapper.toUserEntity(userRequestCreation);
        userEntity.setPassword(passwordEncoder.encode(userRequestCreation.getPassword())); // Hash the password before saving
        
        // Save the new user to the database
        UserEntity savedUser = userRepository.save(userEntity);

        // Convert saved UserEntity to UserResponse and return
        return userMapper.toUserResponse(savedUser);
    }
    
    public UserResponse getUserById(String id) {
        UserEntity userEntity = userRepository.findById(java.util.UUID.fromString(id))
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(userEntity);
    }

    public UserResponse getMyInfo(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(userEntity);

    }

}
