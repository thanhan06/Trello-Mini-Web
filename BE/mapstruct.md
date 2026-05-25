<properties>
    <org.mapstruct.version>1.5.5.Final</org.mapstruct.version>
    <lombok.version>1.18.30</lombok.version> <!-- Tùy phiên bản bạn dùng -->
</properties>

<dependencies>
    <!-- MapStruct Dependency -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${org.mapstruct.version}</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source> <!-- Bằng version Java của bạn -->
                <target>17</target>
                <annotationProcessorPaths>
                    <!-- Chú ý thứ tự: Lombok phải đặt trước -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    <!-- BẮT BUỘC: Binding giữ lombok và mapstruct -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>0.2.0</version> 
                    </path>
                    <!-- MapStruct Processor -->
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${org.mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>

Hoàn toàn có cách. Nếu bạn thấy MapStruct cấu hình lằng nhằng hoặc dễ sinh lỗi compile, bạn có thể bỏ hẳn thư viện này và dùng **3 cách thay thế phổ biến** dưới đây.

### 1. Code thủ công (Manual Mapping / Khuyên dùng nhất)
Đây là cách an toàn nhất, dễ debug nhất và hiệu năng (performance) nhanh nhất (vì không dùng reflection giống các thư viện khác). Bạn tự viết hàm `get`/`set` hoặc dùng `@Builder` của Lombok.

Xóa `@Mapper` đi và tự viết class Mapper:

```java
import org.springframework.stereotype.Component;

@Component
public class ProductManualMapper {

    public ProductResponse toResponse(MstProductEntity entity) {
        if (entity == null) {
            return null;
        }

        ProductResponse response = new ProductResponse();
        // Set các trường cơ bản
        response.setProductId(entity.getProductId());
        response.setStatus(entity.getStatus());
        
        // Map trường lồng nhau (Nested properties) một cách vô cùng rõ ràng
        if (entity.getProductType() != null) {
            response.setProducttypeId(entity.getProductType().getProducttypeId());
            response.setProducttypeName(entity.getProductType().getName());
        }

        if (entity.getCreatedBy() != null) {
            response.setCreateUser(entity.getCreatedBy().getPsnCd());
        }

        return response;
    }
}
```

---

### 2. Dùng thư viện ModelMapper
`ModelMapper` là thư viện phổ biến thứ 2 sau MapStruct. Khác với MapStruct (sinh file lúc compile), ModelMapper dùng **Reflection lúc Runtime** để tự tìm và map dữ liệu. Bạn **không cần cấu hình thêm plugin** vào pom.xml, chỉ cần add dependency là chạy.

**Thêm thư viện vào pom.xml:**
```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.1.1</version>
</dependency>
```

**Cách dùng:**
```java
@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
```
Và trong Service, bạn gọi:
```java
ProductResponse response = modelMapper.map(entity, ProductResponse.class);
```
*(Lưu ý: ModelMapper map tự động theo tên rất thông minh, nhưng hiệu năng sẽ chậm hơn MapStruct và code thủ công một chút).*

---

### 3. Dùng `BeanUtils` có sẵn của Spring Boot
Nếu trong project bạn dùng Spring Boot, bạn có sẵn `BeanUtils`. Tuy nhiên, cách này **chỉ dùng tốt cho các Object có cấu trúc y hệt nhau**, nó không tự hiểu được nested property (kiểu `productType.name` -> `producttypeName`).

```java
import org.springframework.beans.BeanUtils;

public ProductResponse toResponse(MstProductEntity entity) {
    ProductResponse response = new ProductResponse();
    // Copy toàn bộ các trường cùng tên, cùng kiểu
    BeanUtils.copyProperties(entity, response);
    
    // Tự xử lý thủ công các trường không macth tên (như nested object)
    if (entity.getProductType() != null) {
        response.setProducttypeName(entity.getProductType().getName());
    }
    
    return response;
}
```

### 💡 Lời khuyên
- **Dự án nhỏ/Trung bình:** Nên tự code tay (Cách 1) hoặc dùng `@Builder` để khỏi đau đầu với cài đặt thư viện. Code tay giúp bạn dễ dàng Ctrl+Click (hoặc Command+Click) để xem biến đó đã dùng ở đâu.
- **Dự án lớn, cực nhiều entity:** Hãy cố gắng thiết lập lại file pom.xml vài phút để dùng MapStruct, nó vẫn là thư viện tối ưu và tốt nhất cho hệ thống lớn.