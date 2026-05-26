Dưới đây là trọn gói **từng bước một từ A-Z** để bạn làm tính năng này với mô hình **Spring Boot MVC + Thymeleaf**, chạy hoàn toàn tại Local, sử dụng thư mục `upload/` ngang hàng với dự án và lưu tên file cố định vào Database để demo cho khách hàng.

---

### Bước 1: Tạo thư mục `upload` và bỏ ảnh vào

1. Bạn mở thư mục dự án của mình ra (nơi chứa file `pom.xml`).
2. Tạo một thư mục mới tên là **`upload`** ngay tại đó.
3. Copy bức ảnh của bạn vào trong thư mục này và đổi tên chính xác thành **`12455-anhddn-338.jpg`**.

Cấu trúc thư mục của bạn lúc này phải chuẩn như sau:

```text
tên-project-của-bạn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       └── templates/   <-- Nơi chứa các file HTML Thymeleaf
├── upload/                  <-- Thư mục bạn vừa tạo
│   └── 12455-anhddn-338.jpg <-- Ảnh của bạn nằm ở đây
└── pom.xml

```

---

### Bước 2: Tạo File Cấu Hình Để Spring Boot Đọc Thư Mục Ngoài

Mặc định Spring Boot không tự đọc thư mục `upload` nằm ngoài. Bạn cần tạo một class cấu hình để dẫn đường cho nó.

Tạo file `WebConfig.java` trong package cấu hình của bạn:

```java
package com.example.demo.config; // Thay bằng package dự án của bạn

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Khi gọi đường dẫn bắt đầu bằng /upload/ trên trình duyệt
        // Spring Boot sẽ vào thư mục vật lý "upload/" ở gốc dự án để tìm file
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:upload/");
    }
}

```

---

### Bước 3: Cập nhật đường dẫn ảnh vào Database

Mở công cụ quản lý DB của bạn lên (DBeaver, MySQL Workbench, Navicat...), tìm đến bảng dữ liệu bạn cần hiển thị ảnh và sửa giá trị của cột lưu ảnh thành:
👉 **`upload/12455-anhddn-338.jpg`**

*(Nếu lười gõ tay, bạn có thể chạy câu lệnh SQL này)*:

```sql
UPDATE ten_bang_cua_ban 
SET cot_chua_anh = 'upload/12455-anhddn-338.jpg' 
WHERE id = 1; -- Thay bằng ID bản ghi bạn muốn test

```

---

### Bước 4: Viết Controller gửi dữ liệu ra Thymeleaf

Bạn cần một Controller nhận request từ trình duyệt, lấy dữ liệu từ DB (hoặc fake tạm một Object) rồi truyền qua cho Thymeleaf render.

```java
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    // Giả sử bạn lấy từ DB lên được chuỗi này, ở đây mình tạo biến String demo cho nhanh
    @GetMapping("/demo-anh")
    public String viewDemo(Model model) {
        
        // Chuỗi này lấy từ Database ra: "upload/12455-anhddn-338.jpg"
        String imageFromDB = "upload/12455-anhddn-338.jpg"; 
        
        // Đẩy chuỗi này vào Model với cái tên là "anhKhachHang"
        model.addAttribute("anhKhachHang", imageFromDB);
        
        return "demo"; // Sẽ tìm file demo.html trong thư mục templates
    }
}

```

---

### Bước 5: Viết code gọi ảnh trong file HTML Thymeleaf

Bạn vào thư mục `src/main/resources/templates/`, tạo file **`demo.html`** và viết thẻ `<img>` sử dụng cú pháp `th:src` của Thymeleaf như sau:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Demo Hiển Thị Ảnh</title>
</head>
<body>
    <h1>Giao diện Demo cho Khách Hàng</h1>
    
    <!-- 
      Dùng cú pháp |/...| của Thymeleaf để tự động nối dấu / vào trước biến.
      Khi chạy, Thymeleaf sẽ render ra HTML thuần là: <img src="/upload/12455-anhddn-338.jpg">
    -->
    <img th:src="@{|/${anhKhachHang}|}" alt="Ảnh đại diện của khách" width="300"/>

</body>
</html>

```

---

### Bước 6: Chạy thử và hưởng thành quả

1. Bạn nhấn **Run/Restart** lại ứng dụng Spring Boot.
2. Mở trình duyệt web lên và truy cập vào đường dẫn:
👉 `http://localhost:8080/demo-anh`

Ứng dụng sẽ mở trang HTML lên và bức ảnh nằm trong thư mục `upload` ngoài dự án sẽ hiển thị mượt mà trên giao diện trước mặt khách hàng. Mọi thứ hoạt động ăn khớp y hệt như bạn đã code xong tính năng upload thật!