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
@Table(name = "mstproduct")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MstProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mstproduct_product_id_seq")
    @SequenceGenerator(name = "mstproduct_product_id_seq", sequenceName = "mstproduct_product_id_seq", allocationSize = 1)
    @Column(name = "product_id")
    Long productId;

    @Column(name = "product_name", length = 200)
    String productName;

    @Column(name = "status")
    Boolean status;

    @Column(name = "description", length = 400)
    String description;

    @Column(name = "product_img", length = 500)
    String productImg;

    @Column(name = "product_amount")
    Integer productAmount;

    @Column(name = "price")
    Long price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producttype_id")
    MstProductTypeEntity productType;

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
