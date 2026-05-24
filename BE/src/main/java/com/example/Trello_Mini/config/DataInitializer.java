package com.example.Trello_Mini.config;

import com.example.Trello_Mini.entity.Shop.MstProductEntity;
import com.example.Trello_Mini.entity.Shop.MstProductTypeEntity;
import com.example.Trello_Mini.repository.Shop.MstProductRepository;
import com.example.Trello_Mini.repository.Shop.MstProductTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(MstProductRepository productRepository, MstProductTypeRepository productTypeRepository) {
        return args -> {
            if (productTypeRepository.count() == 0) {
                MstProductTypeEntity electronics = MstProductTypeEntity.builder().name("Điện tử").status(true).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
                MstProductTypeEntity computers = MstProductTypeEntity.builder().name("Máy tính").status(true).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
                MstProductTypeEntity accessories = MstProductTypeEntity.builder().name("Phụ kiện").status(true).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
                MstProductTypeEntity appliances = MstProductTypeEntity.builder().name("Điện gia dụng").status(true).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
                
                productTypeRepository.save(electronics);
                productTypeRepository.save(computers);
                productTypeRepository.save(accessories);
                productTypeRepository.save(appliances);

                if (productRepository.count() == 0) {
                    productRepository.save(createProduct("Điện thoại iPhone 15", 22000000L, electronics));
                    productRepository.save(createProduct("Điện thoại Samsung S24", 20000000L, electronics));
                    productRepository.save(createProduct("Laptop Dell XPS", 35000000L, computers));
                    productRepository.save(createProduct("Laptop MacBook Air", 26000000L, computers));
                    productRepository.save(createProduct("Tai nghe AirPods 3", 4500000L, accessories));
                    productRepository.save(createProduct("Chuột Logitech MX Master 3", 2500000L, accessories));
                    productRepository.save(createProduct("Bàn phím cơ Keychron K2", 1800000L, accessories));
                    productRepository.save(createProduct("Tivi Sony 4K 55 inch", 15000000L, appliances));
                    productRepository.save(createProduct("Tủ lạnh Panasonic", 12000000L, appliances));
                    productRepository.save(createProduct("Máy giặt LG", 9500000L, appliances));
                    productRepository.save(createProduct("Sạc dự phòng Anker", 800000L, accessories));
                    productRepository.save(createProduct("Đồng hồ Apple Watch Series 9", 10500000L, electronics));
                    productRepository.save(createProduct("Loa Bluetooth JBL", 3200000L, accessories));
                    productRepository.save(createProduct("Máy hút bụi Dyson", 18000000L, appliances));
                    productRepository.save(createProduct("Nồi cơm điện Cuckoo", 2800000L, appliances));
                    
                    System.out.println("Đã khởi tạo 15 sản phẩm mẫu thành công!");
                }
            }
        };
    }

    private MstProductEntity createProduct(String name, Long price, MstProductTypeEntity type) {
        return MstProductEntity.builder()
                .productName(name)
                .price(price)
                .productType(type)
                .productAmount(100)
                .status(true)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }
}