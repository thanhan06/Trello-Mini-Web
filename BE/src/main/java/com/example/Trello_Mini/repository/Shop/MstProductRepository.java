package com.example.Trello_Mini.repository.Shop;

import com.example.Trello_Mini.entity.Shop.MstProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MstProductRepository extends JpaRepository<MstProductEntity, Long> {

    @Query(value = """
                SELECT p FROM MstProductEntity p
                WHERE (cast(:name as text) IS NULL OR LOWER(p.productName) LIKE cast(:name as text))
                  AND (:typeId IS NULL OR p.productType.producttypeId = :typeId)
                  AND (cast(:desc as text) IS NULL OR LOWER(p.description) LIKE cast(:desc as text))
            """)
    Page<MstProductEntity> search(
            @Param("name") String name,
            @Param("typeId") Long typeId,
            @Param("desc") String desc,
            Pageable pageable);
}