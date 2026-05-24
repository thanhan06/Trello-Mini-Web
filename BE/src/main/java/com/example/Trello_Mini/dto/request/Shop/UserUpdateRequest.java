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
public class UserUpdateRequest {

    @NotBlank(message = "COMMON_VALIDATION_FAILED")
    String userId;

    @NotBlank(message = "COMMON_VALIDATION_FAILED")
    String username;

    @NotBlank(message = "COMMON_VALIDATION_FAILED")
    String password;

    @NotNull(message = "COMMON_VALIDATION_FAILED")
    Short role;

    @NotNull(message = "COMMON_VALIDATION_FAILED")
    Boolean status;

    Integer actorPsnCd;
}
