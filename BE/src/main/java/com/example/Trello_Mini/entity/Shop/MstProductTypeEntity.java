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
@Table(name = "mstproducttype")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MstProductTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mstproducttype_producttype_id_seq")
    @SequenceGenerator(
            name = "mstproducttype_producttype_id_seq",
            sequenceName = "mstproducttype_producttype_id_seq",
            allocationSize = 1)
    @Column(name = "producttype_id")
    Integer producttypeId;

    @Column(name = "name", length = 200)
    String name;

    @Column(name = "status")
    Boolean status;

    @Column(name = "createtime")
    LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "create_user")
    MstUserEntity createdBy;

    @Column(name = "updatetime")
    LocalDateTime updateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "update_user")
    MstUserEntity updatedBy;
}
