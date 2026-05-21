1) Chạy backend ở chế độ debug  
- Trong thư mục BE chạy:
  - `.mvnw.cmd spring-boot:run -D"spring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"`
- Kiểm tra thấy dòng: `Listening for transport dt_socket at address: 5005`

2) Attach debugger trong VS Code  
- Mở **Run and Debug** (`Ctrl+Shift+D`)
- Chọn cấu hình **Attach to Spring Boot (5005)**
- Bấm **Start (F5)**
- Nếu nó hỏi “already running” → bấm **Cancel** (vì đang attach rồi)

3) Đặt breakpoint đúng chỗ (backend)  
Mở file và click vào “lề trái” cạnh số dòng để tạo chấm đỏ:
- Controller nhận request:
  - AuthenticationController.java
  - Đặt breakpoint trong method `login(...)` (ngay dòng gọi `authenticationService.authenticate(request)`)
- Service xử lý đăng nhập:
  - `AuthenticationServiceImpl.authenticate(...)`
  - Đặt breakpoint ở:
    - `userRepository.findByEmail(...)`
    - `passwordEncoder.matches(...)`
    - đoạn tạo token/response

4) Test hàm login để “chạy vào breakpoint”  
Cách A (khuyên dùng): từ PowerShell
- `Invoke-RestMethod -Method Post -Uri http://localhost:8080/auth/login -ContentType application/json -Body '{"email":"test@gmail.com","password":"12345678"}'`

Cách B: từ trình duyệt
- Mở `http://localhost:8080/`
- Nhập email/password rồi bấm “Đăng nhập”

5) Khi dừng ở breakpoint, bạn kiểm tra gì  
- Trong VS Code mục **Variables**:
  - `request.getEmail()`, `request.getPassword()`
  - `user` có null không
  - `authenticated` true/false
  - token được tạo ra chưa
- Dùng nút debug:
  - **Step Over (F10)**: chạy qua từng dòng
  - **Step Into (F11)**: chui vào hàm
  - **Continue (F5)**: chạy tiếp tới breakpoint sau

6) Đọc kết quả trả về để biết “API đúng chưa”  
- Nếu email chưa tồn tại → backend trả `404 USER_NOT_FOUND` (đúng theo code hiện tại)
- Sai mật khẩu → `401 LOGIN_FAILED`
- Đúng → `200` và có `data.accessToken`, `data.refreshToken`

Nếu bạn muốn mình chỉ đúng file `AuthenticationServiceImpl.java` nằm ở đâu để đặt breakpoint (đường dẫn chính xác), nói mình bạn đang dùng workspace nào (bạn có 2 cây src và src).

Để “thấy giá trị `user`” khi bạn đã chạy tới `AuthenticationServiceImpl.authenticate(...)`, bạn làm như sau (đang debug trong VS Code):

1) Đặt breakpoint đúng dòng có biến `user`
Ví dụ trong `authenticate(...)` có đoạn kiểu:
- `var user = userRepository.findByEmail(request.getEmail())...`

Click vào lề trái (gutter) ngay dòng đó để hiện chấm đỏ.

2) Gửi request login để code dừng tại breakpoint
- Bấm login trên web, hoặc dùng PowerShell gọi `POST /auth/login`.

3) Khi debugger dừng lại → xem `user` ở Variables
- Mở panel **Run and Debug**
- Mở tab **Variables**
  - **Local** → bạn sẽ thấy các biến local như `request`, `user`, `authenticated`…
- Bấm mở rộng `user` để xem field: `id`, `email`, `password`, `role`, …

Nếu bạn không thấy `user` trong Local:
- Có thể bạn đang dừng *trước khi dòng tạo user chạy*.
  - Nhấn **F10 (Step Over)** 1 lần để chạy qua dòng đó, lúc đó biến `user` mới xuất hiện.
- Hoặc bạn dừng ở dòng khác chưa vào hàm `authenticate`:
  - Nhấn **F11 (Step Into)** tại `authenticationService.authenticate(request)` để vào trong.

4) Cách xem nhanh bằng Watch / Evaluate (rất tiện)
- **Watch**: bấm dấu `+` rồi nhập:
  - `user`
  - `user.getEmail()`
  - `user.getId()`
- **Debug Console** (hoặc Evaluate Expression):
  - gõ `user` rồi Enter
  - gõ `request.getEmail()` để xem email đang login

5) Nếu `user` bị “null” hoặc bị ném exception trước khi gán
Trong code của bạn, `findByEmail(...).orElseThrow(...)` sẽ **ném exception** nếu không thấy user.
Khi đó:
- Đặt breakpoint *ngay trước* `orElseThrow` (hoặc đặt breakpoint ở dòng `findByEmail`)
- Hoặc xem biến `request.getEmail()` để chắc email đúng
- Và nhớ: nếu chưa đăng ký user thì sẽ luôn vào nhánh throw.

Nếu bạn chụp 5–10 dòng quanh đoạn `var user = ...` trong `AuthenticationServiceImpl.authenticate`, mình chỉ bạn chính xác nên đặt breakpoint ở dòng nào để chắc chắn nhìn được `user` (kể cả case “không tìm thấy user”).