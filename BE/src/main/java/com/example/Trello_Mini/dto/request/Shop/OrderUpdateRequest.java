package com.example.Trello_Mini.dto.request.Shop;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
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
public class OrderUpdateRequest {

    @NotBlank(message = "COMMON_VALIDATION_FAILED")
    String orderStatus;

    String orderDeliveryAddress;

    LocalDateTime orderDeliveryDate;

    Integer actorPsnCd;
}
