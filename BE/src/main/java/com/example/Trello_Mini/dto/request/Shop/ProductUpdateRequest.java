package com.example.Trello_Mini.dto.request.Shop;

import jakarta.validation.constraints.NotBlank;
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
public class ProductUpdateRequest {

    @NotBlank(message = "COMMON_VALIDATION_FAILED")
    String productName;

    @NotNull(message = "COMMON_VALIDATION_FAILED")
    Boolean status;

    String description;

    String productImg;

    @NotNull(message = "COMMON_VALIDATION_FAILED")
    Integer productAmount;

    @NotNull(message = "COMMON_VALIDATION_FAILED")
    Long price;

    @NotNull(message = "COMMON_VALIDATION_FAILED")
    Integer producttypeId;

    Integer actorPsnCd;
}
