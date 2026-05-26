Đoạn code bạn chọn sử dụng `@AuthenticationPrincipal UserDetails userDetails` để lấy thông tin của người dùng đang đăng nhập từ Spring Security Context. Nếu trên máy khác (hoặc môi trường khác) mà tham số này bị `null` hoặc không lấy được dữ liệu, thì đây là các nguyên nhân phổ biến nhất:

1. **Người dùng chưa thực sự Đăng nhập (Missing Session/Token)**
   - Khả năng cao nhất là trên máy đó chưa thực hiện luồng login thành công, hoặc phiên đăng nhập (session) đã hết hạn.
   - Nếu bạn dùng JWT Token cho dự án, việc bạn gõ URL trực tiếp (ví dụ: gõ `http://localhost:8080/shop/product-list` trên trình duyệt) thì trình duyệt sẽ **không** tự động đính kèm Header `Authorization: Bearer <token>`. Do đó, Spring Security coi như là Anonymous User (người dùng ẩn danh) -> `userDetails = null`.

2. **Đường dẫn này đang được nới lỏng cấu hình (permitAll)**
   - Trong file cấu hình bảo mật (`SecurityConfig` / `SecurityFilterChain`), nếu endpoint `"/shop/product-list"` đang được set là `.permitAll()`, Spring Security sẽ không bắt buộc người dùng phải đăng nhập mới được vào trang này. 
   - Những khách vãng lai gọi vào đường dẫn này sẽ không bị chặn, và đương nhiên `userDetails` của họ sẽ bị `null`. Chú ý ở code hiện tại bạn đã xử lý an toàn bằng `if (userDetails != null)` nên nó không văng lỗi màn hình mà chỉ render chuỗi rỗng.

3. **Lỗi chia sẻ Cookie JSESSIONID (khác Domain / IP)**
   - Nếu bạn truy cập bằng cách gõ địa chỉ IP trên máy khác dạng `http://192.168.1.x:8080` (thay vì `localhost`), một số trình duyệt có thể block cookie chứa SessionID do chính sách `SameSite` hoặc `Secure` bị cấu hình ngặt nghèo (đặc biệt khi thiếu HTTPS). Mất cookie = không có Session.

4. **Database trên máy kia chưa có User hoặc khác dữ liệu**
   - Đảm bảo rằng bước đăng nhập trên máy kia thực sự đã thành công dựa trên User có sẵn trong database cụ thể của máy đó. (Bạn có thể xem tab `Network` và `Application -> Cookies/Storage` trong F12 của trình duyệt để xem máy đó đã lưu token/session sau khi login chưa).

**📌 CÁCH GỠ LỖI NHANH (DEBUG):**
Bạn hãy thử đặt một lệnh `System.out.println("User is: " + SecurityContextHolder.getContext().getAuthentication());` ngay dòng đầu tiên của hàm `productList` để in ra terminal xem hiện tại Spring Security đang xác nhận danh tính là ai (đang là `AnonymousAuthenticationToken` hay là thực sự không có gì đó).