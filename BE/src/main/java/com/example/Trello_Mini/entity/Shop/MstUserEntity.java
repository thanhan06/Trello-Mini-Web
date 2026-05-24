package com.example.Trello_Mini.entity.Shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "mstuser")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MstUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mstuser_psn_cd_seq")
    @SequenceGenerator(name = "mstuser_psn_cd_seq", sequenceName = "mstuser_psn_cd_seq", allocationSize = 1)
    @Column(name = "psn_cd")
    Integer psnCd;

    @Column(name = "user_id", length = 8)
    String userId;

    @Column(name = "username", length = 8)
    String username;

    @Column(name = "password", length = 255)
    String password;

    @Column(name = "role")
    Short role;

    @Column(name = "status")
    Boolean status;

    @Column(name = "deletetime")
    LocalDateTime deleteTime;

    @Column(name = "createtime")
    LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "create_psn_cd")
    MstUserEntity createdBy;

    @Column(name = "updatetime")
    LocalDateTime updateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "update_psn_cd")
    MstUserEntity updatedBy;
}
