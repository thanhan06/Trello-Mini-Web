package com.example.Trello_Mini.service.Shop;

import com.example.Trello_Mini.common.ApiException;
import com.example.Trello_Mini.common.ErrorCode;
import com.example.Trello_Mini.dto.request.Shop.ProductTypeCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.ProductTypeUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.ProductTypeResponse;
import com.example.Trello_Mini.entity.Shop.MstProductTypeEntity;
import com.example.Trello_Mini.entity.Shop.MstUserEntity;
import com.example.Trello_Mini.mapper.Shop.ProductTypeMapper;
import com.example.Trello_Mini.repository.Shop.MstProductTypeRepository;
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
public class ProductTypeServiceImpl implements ProductTypeService {

    MstProductTypeRepository productTypeRepository;
    MstUserRepository userRepository;
    ProductTypeMapper productTypeMapper;

    @Override
    public ProductTypeResponse create(ProductTypeCreationRequest request) {
        MstProductTypeEntity entity = productTypeMapper.toEntity(request);
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

        return productTypeMapper.toResponse(productTypeRepository.save(entity));
    }

    @Override
    public ProductTypeResponse update(Integer id, ProductTypeUpdateRequest request) {
        MstProductTypeEntity entity = productTypeRepository
                .findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));

        productTypeMapper.update(entity, request);
        entity.setUpdateTime(LocalDateTime.now());

        if (request.getActorPsnCd() != null) {
            MstUserEntity actor = userRepository
                    .findById(request.getActorPsnCd())
                    .orElseThrow(() -> new ApiException(ErrorCode.MSTUSER_NOT_FOUND));
            entity.setUpdatedBy(actor);
        }

        return productTypeMapper.toResponse(productTypeRepository.save(entity));
    }

    @Override
    public ProductTypeResponse getById(Integer id) {
        return productTypeRepository
                .findById(id)
                .map(productTypeMapper::toResponse)
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));
    }

    @Override
    public List<ProductTypeResponse> list() {
        return productTypeRepository.findAll().stream().map(productTypeMapper::toResponse).toList();
    }

    @Override
    public void delete(Integer id) {
        MstProductTypeEntity entity = productTypeRepository
                .findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));
        productTypeRepository.delete(entity);
    }
}
