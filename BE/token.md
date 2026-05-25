Trường hợp của bạn là một câu hỏi rất phổ biến khi chuyển từ làm API thuần sang làm giao diện trả bằng Thymeleaf/JSP.

Câu trả lời ngắn gọn là: **KHÔNG CẦN THIẾT và KHÔNG NÊN DÙNG JWT** nếu bạn đang làm mô hình MVC truyền thống (Spring Boot + Thymeleaf), nơi mà giao diện (HTML) và logic (Java) chạy trên cùng một server.

### Tại sao KHÔNG NÊN dùng JWT cho mô hình Spring MVC (Thymeleaf)?

1. **JWT sinh ra cho mô hình không lưu trạng thái (Stateless - RestAPI):**
   Mục đích của JWT là dành cho các hệ thống Frontend (như ReactJS, VueJS, Mobile App) nằm tách biệt hoàn toàn so với Backend. Frontend nhận JWT và tự lưu vào `localStorage`, mỗi lần gọi API thì nhét nó vào header `Authorization: Bearer <token>`.
   
   Còn ở MVC (Thymeleaf), trình duyệt chỉ biết mỗi việc: Gửi form đăng nhập -> Nhận file HTML hiển thị. Trình duyệt **không biết tự động nhét token vào header** mỗi lần bạn bấm sang trang khác (như trang Danh sách sản phẩm). Nếu cố chấp dùng JWT, bạn phải viết thêm một đống code Javascript ở Frontend để "kẹp" token vào mỗi request, tự nhiên làm phức tạp hóa vấn đề.

2. **Cách quản lý tốt nhất của MVC là Session Cookie (Stateful):**
   Trong Spring MVC, khi user đăng nhập thành công, Spring sẽ lưu thông tin user vào một vùng nhớ tạm gọi là **Session (JSESSIONID)**. Spring tự động tạo một Cookie chứa ID này ném về trình duyệt. Cookie này tự động chạy theo mỗi lần bạn bấm link hay chuyển trang mà bạn *không cần viết nửa dòng code nào cả*. Khi đóng trình duyệt, Session mất, user bị văng ra.

---

### Nhưng project hiện tại của bạn đang bị cấu hình lai tạp (Nửa nạc nửa mỡ)

Tôi xem file SercurityConfig.java của bạn và thấy cấu hình này:

```java
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
// ... và bên dưới có cấu hình oauth2ResourceServer(oauths -> oauth2.jwt(...))
```

Dòng chữ `SessionCreationPolicy.STATELESS` nghĩa là bạn đã **cấm Spring sử dụng Session**. Điều này khiến mô hình Thymeleaf không thể ghi nhớ người dùng sau khi họ đăng nhập (cứ đăng nhập xong, chuyển sang trang khác là lại bị đá ra vì server không nhớ AI ĐANG REQUEST).

### Lời khuyên cho bạn

**Nếu bạn muốn theo hướng thuần MVC (Thymeleaf):**
1. Xóa cấu hình `.sessionManagement(...) STATELESS` đi hoặc đổi thành `IF_REQUIRED` (mặc định).
2. Xóa cấu hình `oauth2ResourceServer(jwt)`.
3. Bật form login bằng cấu hình đơn giản của Spring: `.formLogin(form -> form.loginPage("/auth/login").permitAll())`.
4. Không cần đụng đến sinh JWT hay parse JWT nữa, cứ dùng hàm `AuthenticationPrincipal UserDetails userDetails` trong Controller như hiện tại là đủ ăn.

**Nếu bạn muốn giữ JWT làm trọng tâm:**
Thì lời khuyên chân thành là hãy **vứt luôn Thymeleaf đi**. Bạn nên cấu hình project thành một Backend API thuần phục vụ JSON. Sau đó code một con VueJS hoặc ReactJS riêng để làm Frontend.

*Nếu bạn làm đồ án nộp bài nhanh, hãy chọn cách 1 (bỏ JWT, dùng Session của mvc), nó sẽ cứu bạn khỏi hàng tá lỗi rắc rối sau này đấy!*