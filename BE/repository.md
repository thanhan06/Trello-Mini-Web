Có, hoàn toàn có thể làm được. Tuy nhiên, vì hàm này của bạn đang trả về phân trang (`Page<MstProductEntity>`), việc kết hợp `FETCH JOIN` với phân trang trong JPA cần một chút cẩn thận để tránh lỗi hoặc cảnh báo từ Hibernate (nổi tiếng với lỗi *lấy toàn bộ dữ liệu ra memory rồi mới phân trang* hoặc *lỗi sai count query*).

Bạn có **2 cách** để làm Fetch Join trong trường hợp này:

### Cách 1: Dùng `@EntityGraph` (Khuyên dùng - Ngắn gọn nhất)
Spring Data JPA cung cấp annotation `@EntityGraph` để giải quyết bài toán "N+1 query" rất gọn gàng. Bạn giữ nguyên `@Query` hiện tại, Spring sẽ tự động hiểu và thêm `LEFT OUTER JOIN` vào câu lệnh SQL thực tế mà không làm hỏng logic đếm tổng số bản ghi (count query) của phân trang.

```java
import org.springframework.data.jpa.repository.EntityGraph;

public interface MstProductRepository extends JpaRepository<MstProductEntity, Long> {

    // Chỉ định thuộc tính nào cần được fetch eagerly
    @EntityGraph(attributePaths = {"productType"}) 
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
```
*(Nếu bạn cần lấy thêm các bảng User nữa, cứ thêm vào mảng: `attributePaths = {"productType", "createdBy"}`)*

---

### Cách 2: Viết `JOIN FETCH` trực tiếp bằng JPQL kèm `countQuery`
Nếu bạn chọn viết tay vào trong `@Query`, khi gặp `JOIN FETCH` cùng với `Pageable`, Spring Data JPA sẽ không thể tự dịch ra câu lệnh `COUNT(...)` để đếm tổng số lượng (dùng cho tính `totalPages`). Bạn bắt buộc phải tự viết thêm `countQuery` (lưu ý count query ko dùng lệnh fetch).

```java
public interface MstProductRepository extends JpaRepository<MstProductEntity, Long> {

    @Query(
        value = """
            SELECT p FROM MstProductEntity p
            LEFT JOIN FETCH p.productType pt
            WHERE (cast(:name as text) IS NULL OR LOWER(p.productName) LIKE cast(:name as text))
              AND (:typeId IS NULL OR pt.producttypeId = :typeId)
              AND (cast(:desc as text) IS NULL OR LOWER(p.description) LIKE cast(:desc as text))
        """,
        countQuery = """
            SELECT count(p) FROM MstProductEntity p
            LEFT JOIN p.productType pt
            WHERE (cast(:name as text) IS NULL OR LOWER(p.productName) LIKE cast(:name as text))
              AND (:typeId IS NULL OR pt.producttypeId = :typeId)
              AND (cast(:desc as text) IS NULL OR LOWER(p.description) LIKE cast(:desc as text))
        """
    )
    Page<MstProductEntity> search(
            @Param("name") String name,
            @Param("typeId") Long typeId,
            @Param("desc") String desc,
            Pageable pageable);
}
```
**Lưu ý cho Cách 2**: Tôi đã sửa `p.productType.producttypeId` thành khai báo `LEFT JOIN FETCH p.productType pt` và sau đó sử dụng alias `pt.producttypeId`.

**Lời khuyên:** Hãy chọn **Cách 1** vì code ngắn hơn rất nhiều và cực kỳ an toàn với Hibernate/Spring Data. Cách mạng.