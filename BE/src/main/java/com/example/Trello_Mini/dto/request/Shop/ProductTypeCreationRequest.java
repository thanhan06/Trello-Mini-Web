package com.example.Trello_Mini.dto.request.Shop;

import jakarta.validation.constraints.NotBlank;
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
public class ProductTypeCreationRequest {

    @NotBlank(message = "COMMON_VALIDATION_FAILED")
    String name;

    Boolean status;

    Integer actorPsnCd;
}
