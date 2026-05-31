package com.example.Trello_Mini.dto.request.Shop;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
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
public class OrderBatchCreationRequest {

    @NotBlank(message = "COMMON_VALIDATION_FAILED")
    String customName;

    @NotBlank(message = "COMMON_VALIDATION_FAILED")
    String orderDeliveryAddress;

    @NotNull(message = "COMMON_VALIDATION_FAILED")
    @JsonFormat(pattern = "yyyy/MM/dd")
    LocalDate orderDeliveryDate;

    @NotEmpty(message = "COMMON_VALIDATION_FAILED")
    @Valid
    List<OrderBatchItemRequest> items;

    String actorUsername;
}