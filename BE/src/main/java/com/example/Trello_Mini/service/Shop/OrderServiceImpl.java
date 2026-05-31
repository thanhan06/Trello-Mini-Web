package com.example.Trello_Mini.service.Shop;

import com.example.Trello_Mini.common.ApiException;
import com.example.Trello_Mini.common.ErrorCode;
import com.example.Trello_Mini.dto.request.Shop.OrderBatchCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.OrderBatchItemRequest;
import com.example.Trello_Mini.dto.request.Shop.OrderCreationRequest;
import com.example.Trello_Mini.dto.request.Shop.OrderUpdateRequest;
import com.example.Trello_Mini.dto.response.Shop.OrderResponse;
import com.example.Trello_Mini.entity.Shop.MstProductEntity;
import com.example.Trello_Mini.entity.Shop.MstUserEntity;
import com.example.Trello_Mini.entity.Shop.TrProductOrderEntity;
import com.example.Trello_Mini.mapper.Shop.OrderMapper;
import com.example.Trello_Mini.repository.Shop.MstProductRepository;
import com.example.Trello_Mini.repository.Shop.MstUserRepository;
import com.example.Trello_Mini.repository.Shop.TrProductOrderRepository;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    TrProductOrderRepository orderRepository;
    MstProductRepository productRepository;
    MstUserRepository userRepository;
    OrderMapper orderMapper;

    @Override
    public OrderResponse create(OrderCreationRequest request) {
        MstProductEntity product = productRepository
                .findById(request.getOrderProductId())
                .orElseThrow(() -> new ApiException(ErrorCode.PRODUCT_NOT_FOUND));

        long unitPrice = product.getPrice() != null ? product.getPrice() : 0L;
        long totalPrice = unitPrice * request.getOrderProductAmount();

        LocalDateTime now = LocalDateTime.now();
        TrProductOrderEntity entity = TrProductOrderEntity.builder()
                .customName(request.getCustomName())
                .product(product)
                .orderProductAmount(request.getOrderProductAmount())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .orderStatus(request.getOrderStatus() != null ? request.getOrderStatus() : "NEW")
                .orderDeliveryAddress(request.getOrderDeliveryAddress())
                .orderDeliveryDate(request.getOrderDeliveryDate())
                .createTime(now)
                .updateTime(now)
                .build();

        if (request.getActorPsnCd() != null) {
            MstUserEntity actor = userRepository
                    .findById(request.getActorPsnCd())
                    .orElseThrow(() -> new ApiException(ErrorCode.MSTUSER_NOT_FOUND));
            entity.setCreatedBy(actor);
            entity.setUpdatedBy(actor);
        }

        return orderMapper.toResponse(orderRepository.save(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<OrderResponse> createBatch(OrderBatchCreationRequest request) {
        Map<Long, Integer> requestedQuantities = request.getItems().stream()
                .collect(Collectors.toMap(
                        OrderBatchItemRequest::getOrderProductId,
                        OrderBatchItemRequest::getOrderProductAmount,
                        Integer::sum,
                        LinkedHashMap::new));

        List<MstProductEntity> products = productRepository.findAllById(requestedQuantities.keySet()).stream().toList();
        Map<Long, MstProductEntity> productMap = products.stream()
                .collect(Collectors.toMap(MstProductEntity::getProductId, Function.identity()));

        for (Long productId : requestedQuantities.keySet()) {
            if (!productMap.containsKey(productId)) {
                throw new ApiException(ErrorCode.PRODUCT_NOT_FOUND);
            }
        }

        MstUserEntity actor = null;
        if (request.getActorUsername() != null && !request.getActorUsername().isBlank()) {
            actor = userRepository
                    .findByUsername(request.getActorUsername())
                    .orElseThrow(() -> new ApiException(ErrorCode.MSTUSER_NOT_FOUND));
        }

        LocalDateTime deliveryDateTime = request.getOrderDeliveryDate().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<TrProductOrderEntity> entities = new java.util.ArrayList<>();
        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            MstProductEntity product = productMap.get(entry.getKey());
            int available = Math.max(0, (product.getProductAmount() != null ? product.getProductAmount() : 0)
                    - (product.getOrderProductAmount() != null ? product.getOrderProductAmount() : 0));

            if (entry.getValue() > available) {
                throw new ApiException(ErrorCode.ORDER_ITEM_NOT_ENOUGH);
            }

            long unitPrice = product.getPrice() != null ? product.getPrice() : 0L;
            long totalPrice = unitPrice * entry.getValue();

            TrProductOrderEntity entity = TrProductOrderEntity.builder()
                    .customName(request.getCustomName())
                    .product(product)
                    .orderProductAmount(entry.getValue())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .orderStatus("NEW")
                    .orderDeliveryAddress(request.getOrderDeliveryAddress())
                    .orderDeliveryDate(deliveryDateTime)
                    .createTime(now)
                    .updateTime(now)
                    .build();

            if (actor != null) {
                entity.setCreatedBy(actor);
                entity.setUpdatedBy(actor);
            }

            entities.add(entity);
        }

        return orderRepository.saveAll(entities).stream().map(orderMapper::toResponse).toList();
    }

    @Override
    public OrderResponse update(Long id, OrderUpdateRequest request) {
        TrProductOrderEntity entity = orderRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.ORDER_NOT_FOUND));

        entity.setOrderStatus(request.getOrderStatus());
        entity.setOrderDeliveryAddress(request.getOrderDeliveryAddress());
        entity.setOrderDeliveryDate(request.getOrderDeliveryDate());
        entity.setUpdateTime(LocalDateTime.now());

        if (request.getActorPsnCd() != null) {
            MstUserEntity actor = userRepository
                    .findById(request.getActorPsnCd())
                    .orElseThrow(() -> new ApiException(ErrorCode.MSTUSER_NOT_FOUND));
            entity.setUpdatedBy(actor);
        }

        return orderMapper.toResponse(orderRepository.save(entity));
    }

    @Override
    public OrderResponse getById(Long id) {
        return orderRepository
                .findById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new ApiException(ErrorCode.ORDER_NOT_FOUND));
    }

    @Override
    public List<OrderResponse> list() {
        return orderRepository.findAll().stream().map(orderMapper::toResponse).toList();
    }

    @Override
    public void delete(Long id) {
        TrProductOrderEntity entity = orderRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.ORDER_NOT_FOUND));
        orderRepository.delete(entity);
    }
}
