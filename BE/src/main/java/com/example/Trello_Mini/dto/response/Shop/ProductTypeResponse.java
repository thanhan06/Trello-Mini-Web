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
public class ProductTypeResponse {
    Integer producttypeId;
    String name;
    Boolean status;
    LocalDateTime createTime;
    Integer createUser;
    LocalDateTime updateTime;
    Integer updateUser;
}
