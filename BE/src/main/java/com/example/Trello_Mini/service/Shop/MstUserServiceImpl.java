package com.example.Trello_Mini.service.Shop;

import com.example.Trello_Mini.common.ApiException;
import com.example.Trello_Mini.common.ErrorCode;
import com.example.Trello_Mini.dto.request.Shop.UserCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.UserUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.UserResponse;
import com.example.Trello_Mini.entity.Shop.MstUserEntity;
import com.example.Trello_Mini.mapper.Shop.UserShopMapper;
import com.example.Trello_Mini.repository.Shop.MstUserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MstUserServiceImpl implements MstUserService {

    MstUserRepository userRepository;
    UserShopMapper userMapper;

    @Override
    public UserResponse create(UserCreationRequest request) {
        MstUserEntity entity = userMapper.toEntity(request);
        if (entity.getStatus() == null) {
            entity.setStatus(false);
        }

        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        if (request.getActorPsnCd() != null) {
            MstUserEntity actor = userRepository
                    .findById(request.getActorPsnCd())
                    .orElseThrow(() -> new ApiException(ErrorCode.MSTUSER_NOT_FOUND));
            entity.setCreatedBy(actor);
            entity.setUpdatedBy(actor);
        }

        return userMapper.toResponse(userRepository.save(entity));
    }

    @Override
    public UserResponse update(Integer psnCd, UserUpdateRequest request) {
        MstUserEntity entity = userRepository.findById(psnCd).orElseThrow(() -> new ApiException(ErrorCode.MSTUSER_NOT_FOUND));

        userMapper.update(entity, request);
        entity.setUpdateTime(LocalDateTime.now());

        if (request.getActorPsnCd() != null) {
            MstUserEntity actor = userRepository
                    .findById(request.getActorPsnCd())
                    .orElseThrow(() -> new ApiException(ErrorCode.MSTUSER_NOT_FOUND));
            entity.setUpdatedBy(actor);
        }

        return userMapper.toResponse(userRepository.save(entity));
    }

    @Override
    public UserResponse getById(Integer psnCd) {
        return userRepository
                .findById(psnCd)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ApiException(ErrorCode.MSTUSER_NOT_FOUND));
    }

    @Override
    public UserResponse getByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ApiException(ErrorCode.MSTUSER_NOT_FOUND));
    }

    @Override
    public List<UserResponse> list() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    @Override
    public void delete(Integer psnCd) {
        MstUserEntity entity = userRepository.findById(psnCd).orElseThrow(() -> new ApiException(ErrorCode.MSTUSER_NOT_FOUND));
        userRepository.delete(entity);
    }
}
