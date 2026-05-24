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
@Table(name = "trproductorder")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrProductOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trproductorder_id_seq")
    @SequenceGenerator(name = "trproductorder_id_seq", sequenceName = "trproductorder_id_seq", allocationSize = 1)
    @Column(name = "id")
    Long id;

    @Column(name = "custom_name", length = 200)
    String customName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_product_id")
    MstProductEntity product;

    @Column(name = "order_product_amount")
    Integer orderProductAmount;

    @Column(name = "unit_price")
    Long unitPrice;

    @Column(name = "total_price")
    Long totalPrice;

    @Column(name = "order_status", length = 50)
    String orderStatus;

    @Column(name = "order_delivery_address", length = 400)
    String orderDeliveryAddress;

    @Column(name = "order_delivery_date")
    LocalDateTime orderDeliveryDate;

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
