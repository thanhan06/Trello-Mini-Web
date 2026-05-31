package com.example.Trello_Mini.dto.request.Shop;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderBatchItemRequest {

    @NotNull(message = "COMMON_VALIDATION_FAILED")
    Long orderProductId;

    @NotNull(message = "COMMON_VALIDATION_FAILED")
    @Min(value = 1, message = "COMMON_VALIDATION_FAILED")
    Integer orderProductAmount;
}