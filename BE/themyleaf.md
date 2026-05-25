Để dùng được Thymeleaf trong một project Spring Boot, bạn bắt buộc phải cài đặt dependency và tuân thủ một số quy tắc về thư mục. Lỗi khi cấu hình project mới thường rơi vào 3 nguyên nhân sau đây:

### 1. Phải khai báo thư viện trong pom.xml
Nếu bạn quên cài thư viện này, Spring Boot sẽ không biết cách "dịch" file HTML theo chuẩn Thymeleaf, dẫn đến việc nó báo lỗi 404 (Không tìm thấy trang) hoặc 500 (Template Resolution Exception).

Bạn cần thêm đoạn này vào file pom.xml (nếu dùng Maven):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```
*(Nếu bạn dùng Gradle, thêm `implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'` vào file `build.gradle`)*.

### 2. Vị trí đặt file HTML phải chuẩn mực
Mặc định, Thymeleaf trong Spring Boot chỉ quét (scan) các file giao diện ở một thư mục duy nhất. Bạn **bắt buộc** phải đặt file `.html` vào đúng đường dẫn sau:
👉 templates

Nếu bạn đặt file ở ngoài (ví dụ: vứt trong thư mục `static/` hay cấu trúc khác), thì Spring sẽ báo lỗi Template might not exist.

### 3. Java Controller phải dùng `@Controller`, không dùng `@RestController`
Lỗi kinh điển nhất mà mọi người hay gặp là xài nhầm Annotation.
- Nếu bạn dùng `@RestController`, lúc bạn `return "index";`, Spring sẽ in thẳng chữ "index" ra màn hình làm chữ màu đen trắng ngớ ngẩn (hoặc báo lỗi không thể parse JSON).
- Bạn **phải dùng `@Controller`** thì khi `return "index";`, Spring mới đi vào thư mục `templates` tìm file `index.html` để render thành một trang web.

```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // <--- Nhớ dùng cái này
public class MyWebController {

    @GetMapping("/home")
    public String homePage() {
        return "index"; // Sẽ tự map với file src/main/resources/templates/index.html
    }
}
```

### 4. Bổ sung namespace trên thẻ `<html>`
Dù không ép buộc nhưng để IDE (VS Code, IntelliJ) gợi ý code chuẩn và không báo lỗi gạch đỏ, bạn nên sửa thẻ `<html>` thấp nhất thành như sau:
```html
<html xmlns:th="http://www.thymeleaf.org">
```

**Tóm lại:** Bạn hãy check lại project bên kia xem 1) Bỏ thư viện vào pom.xml chưa? 2) File HTML có đặt đúng trong `templates` không? và 3) Class Controller đã để đúng chữ `@Controller` chưa nhé!