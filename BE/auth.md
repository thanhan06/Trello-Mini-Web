Compacted conversationThực tế là **bạn không thể gắn custom HTTP Header (như `Authorization: Bearer ...`) vào các thao tác điều hướng URL thông thường** (như click thẻ `<a>` hoặc submit `<form>`).

Thymeleaf chỉ là công cụ render mã HTML ở phía Server. Khi trang HTML đã tải xuống trình duyệt, các thao tác chuyển trang đều là hành vi mặc định của browser. Trình duyệt không hỗ trợ việc tuỳ chỉnh Header cho các request chuyển trang đó. 

Do đó, đối với các ứng dụng dùng SSR (Server-Side Rendering như Thymeleaf), có 3 cách để gửi JWT Token xuống Back-end mỗi khi chuyển trang:

### 1. Sử dụng Cookie (Đây là cách chuẩn nhất & chúng ta đang dùng)
Trình duyệt **tự động đính kèm Cookie** vào mọi request gửi lên server (kể cả khi bạn click `<a href="...">` hay submit `<form>`).
* **Tại Frontend**: Sau khi login thành công và lấy được JWT, bạn lưu JWT đó vào Cookie (như chúng ta đã code: `document.cookie = "accessToken=" + token`).
* **Tại Backend**: Chúng ta đã cấu hình Spring Security (thông qua `BearerTokenResolver`) để tự động vào Cookie tìm `accessToken` thay vì chỉ khăng khăng đọc Header.

### 2. Dùng JavaScript (AJAX / Fetch) thay vì Submit mặc định
Nếu bắt buộc phải dùng Header, bạn không được dùng thẻ `<a>` chuyển trang hay form submit mặc định nữa, mà phải dùng Javascript chặn sự kiện lại, sau đó dùng `fetch` hoặc `axios` để gọi API.

```javascript
document.querySelector('#my-link').addEventListener('click', function(e) {
    e.preventDefault(); // Chặn tải lại trang
    
    fetch('/shop/product-list', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
    }).then(response => {
        // Render lại trang web bằng Javascript
    });
})
```
*Nhược điểm:* Mất đi cái hay của Thymeleaf (dễ SEO, đơn giản), làm giao diện phức tạp hoá (biến thành Single Page Application - React/Vue).

### 3. Gửi Token qua tham số URL hoặc Hidden Input (Không khuyến khích)
Bạn có thể nhét token vào URL (ví dụ: `<a href="/shop/product-list?token=eyJhb...">`) hoặc thẻ input ẩn trong Form (`<input type="hidden" name="token" value="...">`).
*Tại Backend:* Viết filter đọc token từ biến `token` của request.
*Nhược điểm:* **Cực kỳ kém bảo mật**. Thay vì ẩn, Token của bạn sẽ phơi bày rõ ràng trên thanh địa chỉ URL, rất dễ bị hack hoặc rò rỉ khi người dùng copy/paste link cho nhau.

---

**Tóm lại:** Việc bạn sử dụng **Cookie** song song với JWT (như đã setup ở các bước trước) chính là giải pháp tối ưu, an toàn và đúng đắn nhất dành cho việc bảo mật khi dùng render HTML dùng Thymeleaf!



Dựa vào kiến trúc hệ thống bạn đang sử dụng (Spring Boot + Thymeleaf + JWT lưu trong Cookie), đây là toàn bộ luồng hoạt động từ lúc người dùng đăng nhập cho đến khi vào xem danh sách sản phẩm và cách hệ thống xử lý chuyển hướng khi hết hạn / chưa đăng nhập:

### 1. Luồng đăng nhập (Khởi tạo phiên làm việc)
* **Frontend:** Người dùng nhập Username/Password và nhấn "Đăng nhập". JavaScript dùng `fetch/axios` gọi API Login.
* **Backend:** Server kiểm tra tài khoản, nếu đúng sẽ tạo một **JWT Token** và trả về cho Frontend.
* **Lưu trữ:** JavaScript trên trình duyệt nhận Token và làm 2 việc:
  1. Lưu vào `localStorage` (để dành dùng cho các đoạn code JS tự gọi API bằng Header nếu cần).
  2. Lưu vào **Cookie** (`document.cookie = "accessToken=" + token; path=/`). Đây là bước quan trọng nhất để dùng với Thymeleaf.

### 2. Luồng lấy danh sách sản phẩm (Khi click thẻ `<a>` hoặc phân trang)
Do bạn chuyển trang bằng SSR (Load lại trang trình duyệt) nên luồng hoạt động sẽ như sau:
* **Gửi Request:** Trình duyệt tự động gắn Cookie `accessToken` vào request và gửi lên đường dẫn `/shop/product-list` (hoặc các trang có tham số phân trang `?page=...`).
* **Kiểm tra ở Backend (Spring Security):**
  1. Request đi qua các bộ lọc (Filter) của Spring Security.
  2. Lớp `BearerTokenResolver` (được bạn viết lại - Custom) sẽ vào túi Cookie để lấy ra giá trị `accessToken`.
  3. Server giải mã và xác thực Token này (check chữ ký, check thời hạn expiry).
* **Xử lý logic (Controller):**
  * Nếu Token **Hợp lệ**: Spring Security cho phép request đi tiếp vào Controller. Controller lấy data từ Database, nhét vào Model, gọi Thymeleaf render thành file HTML hoàn chỉnh rồi trả về cho trình duyệt.

### 3. Cách chuyển trang Login khi cần (Chưa đăng nhập / Token hết hạn)
Việc chuyển người dùng về trang Đăng nhập được thực hiện chặn ở cả 2 đầu (Frontend và Backend) để đảm bảo trải nghiệm và bảo mật tốt nhất:

**Chặn ở Backend (Bảo vệ dữ liệu tuyệt đối):**
* Khi request lên `/shop/product-list`, nếu `BearerTokenResolver` không tìm thấy Cookie, hoặc Token đã hết hạn/sai chữ ký $\Rightarrow$ Spring Security ném ra lỗi `AuthenticationException`.
* Spring Security (thông qua `AuthenticationEntryPoint`) sẽ bắt lỗi này và thay vì trả về giao diện lỗi, nó trả về mã trạng thái HTTP **302 Redirect** kèm theo `Location: /` (hoặc url trang login của bạn).
* Trình duyệt nhận mã 302 sẽ tự động load lại và đưa người dùng về trang Đăng nhập.

**Chặn ở Frontend (Trải nghiệm mượt mà, giảm tải Server):**
* Bạn đã gắn một đoạn mã `<script>` nhỏ trên `<head>` của trang `listproduct.html`.
* Đoạn mã này có nhiệm vụ: Ngay khi trình duyệt vừa chuẩn bị render HTML, nó sẽ lấy token từ `localStorage`, giải mã (*base64 decode*) để lấy payload, và kiểm tra trường `exp` (thời gian hết hạn).
* Nếu phát hiện thời gian hiện tại đã vượt qua `exp`, nó lập tức chạy lệnh `window.location.href = '/'` để đuổi người dùng về trang chủ/login mà không cần đợi Server nhắc nhở.

**Tóm lại:** Nhờ sử dụng **Cookie**, trình duyệt tự làm nhiệm vụ "mang Token lên Server" trong mọi cú click chuyển trang. Còn việc xác thực và "đuổi" người dùng về trang Login được Spring Boot lo thông qua cơ chế Redirect 302 của Security!