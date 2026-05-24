package com.example.Trello_Mini.dto.response.Shop;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long productId;
    String productName;
    Boolean status;
    String description;
    String productImg;
    Integer productAmount;
    Long price;
    Integer producttypeId;
    String producttypeName;
    LocalDateTime createTime;
    Integer createUser;
    LocalDateTime updateTime;
    Integer updateUser;
}
