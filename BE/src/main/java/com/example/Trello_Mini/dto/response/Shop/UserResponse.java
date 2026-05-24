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
public class UserResponse {
    Integer psnCd;
    String userId;
    String username;
    Short role;
    Boolean status;
    LocalDateTime createTime;
    Integer createPsnCd;
    LocalDateTime updateTime;
    Integer updatePsnCd;
}
