package com.example.Trello_Mini.service.Shop;

import com.example.Trello_Mini.common.ApiException;
import com.example.Trello_Mini.common.ErrorCode;
import com.example.Trello_Mini.dto.request.Shop.ProductCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.ProductUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.ProductResponse;
import com.example.Trello_Mini.entity.Shop.MstProductEntity;
import com.example.Trello_Mini.entity.Shop.MstProductTypeEntity;
import com.example.Trello_Mini.entity.Shop.MstUserEntity;
import com.example.Trello_Mini.mapper.Shop.ProductMapper;
import com.example.Trello_Mini.repository.Shop.MstProductRepository;
import com.example.Trello_Mini.repository.Shop.MstProductTypeRepository;
import com.example.Trello_Mini.repository.Shop.MstUserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

    MstProductRepository productRepository;
    MstProductTypeRepository productTypeRepository;
    MstUserRepository userRepository;
    ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductCreationRequest request) {
        MstProductTypeEntity productType = productTypeRepository
                .findById(request.getProducttypeId())
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));

        MstProductEntity entity = productMapper.toEntity(request, productType);
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

        return productMapper.toResponse(productRepository.save(entity));
    }

    @Override
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        MstProductEntity entity = productRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_NOT_FOUND));
        MstProductTypeEntity productType = productTypeRepository
                .findById(request.getProducttypeId())
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));

        productMapper.update(entity, request, productType);
        entity.setUpdateTime(LocalDateTime.now());

        if (request.getActorPsnCd() != null) {
            MstUserEntity actor = userRepository
                    .findById(request.getActorPsnCd())
                    .orElseThrow(() -> new ApiException(ErrorCode.MSTUSER_NOT_FOUND));
            entity.setUpdatedBy(actor);
        }

        return productMapper.toResponse(productRepository.save(entity));
    }

    @Override
    public ProductResponse getById(Long id) {
        return productRepository
                .findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public List<ProductResponse> list() {
        return productRepository.findAll().stream().map(productMapper::toResponse).toList();
    }

   @Override
public Page<ProductResponse> getProducts(int pageNo, int pageSize,
                                          String name, String type, String desc) {
    Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "orderProductAmount"));

    String namePram  = (name != null && !name.isBlank()) ? "%" + name.toLowerCase() + "%" : null;
    String descParam = (desc != null && !desc.isBlank()) ? "%" + desc.toLowerCase() + "%" : null;
    Long   typeParam = (type != null && !type.isBlank()) ? Long.parseLong(type) : null;

    return productRepository.search(namePram, typeParam, descParam, pageable)
                            .map(productMapper::toResponse);
}

    @Override
    public void delete(Long id) {
        MstProductEntity entity = productRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(entity);
    }
}
