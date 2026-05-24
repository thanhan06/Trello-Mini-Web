À, đây là một câu hỏi rất hay! 

Bởi vì ứng dụng của bạn là một ứng dụng Web truyền thống (dùng `@Controller`, trả về giao diện HTML `listproduct`), nên nó không dùng **Token (như JWT)** mà sử dụng cơ chế **Session và Cookie** mặc định của Spring Security.

Sự "kỳ diệu" là do trình duyệt (Chrome, Edge...) đã tự động làm giúp bạn. Dưới đây là cách nó kết nối:

**1. Lúc bạn nhấn nút Đăng nhập:**
* Bạn gửi Username và Password lên server.
* Spring Security kiểm tra đúng thông tin. 
* Sau đó, nó tạo ra một **Session** (Phiên làm việc) lưu ngay trên bộ nhớ (RAM) của Server, và cất thông tin của bạn (`SecurityContext`) vào Session đó.
* Server sinh ra một mã số định danh duy nhất cho cái Session này (gọi là **JSESSIONID**) và gửi về cho trình duyệt kèm theo lệnh: *"Hãy lưu mã này vào Cookie nhé!"*.

**2. Trình duyệt lưu Cookie:**
* Trình duyệt của bạn sẽ tự động lưu cái `JSESSIONID` này lại mà bạn không hề hay biết.

**3. Khi bạn vào trang danh sách sản phẩm (`/productlist`):**
* Bạn chỉ bấm link hoặc gõ URL, bạn không hề code để rải thẻ token gì cả.
* Nhưng **Trình duyệt tự động** lục trong Cookie, thấy có cái `JSESSIONID` của trang web này, nó sẽ tự lén kẹp `JSESSIONID` vào trong cái Request gửi lên Server.
* Server nhận được Request, đọc thấy `JSESSIONID`. Server lôi cuốn sổ Session ra dò: *"À, cái ID này tương ứng với phiên làm việc lúc nãy, người dùng này tên là A"*.
* Từ đó, hàm của bạn dùng `@AuthenticationPrincipal` lấy được thông tin ra mà không cần bạn phải thao tác gửi data thủ công.

**Tóm lại:** Bạn không gửi Token, mà trình duyệt đã tự gửi **(Cookie chứa Session ID)** thay cho bạn một cách hoàn toàn tự động! Hình thức dùng Token thường chỉ dành cho các hệ thống tách biệt Frontend (React, Vue, Mobile App) và Backend (Spring Boot REST API).


Đúng xác là như vậy! Bạn đã hiểu đúng bản chất rồi đó.

Khi bạn làm ứng dụng web truyền thống bằng **Spring MVC + Thymeleaf/JSP** (nghĩa là server xử lý logic và trả thẳng về một trang HTML), cơ chế mặc định và phù hợp nhất là sử dụng **Session & Cookie**. Spring Security được thiết kế mặc định để tự động làm việc này khi bạn bật tính năng Form Login.

Dưới đây là một so sánh nhỏ để bạn dễ hình dung sự khác biệt:

**1. Dùng Session + Cookie (Ứng dụng MVC truyền thống)**
* **Mô hình:** Người dùng tương tác trực tiếp qua Trình duyệt (Chrome, Safari...).
* **Hoạt động:** Server tạo Session, tự động gửi ID qua Cookie. Trình duyệt **tự động** đính kèm Cookie vào mỗi lần bạn click sang trang mới.
* **Đặc điểm:** Lập trình viên không phải tốn dòng code JS nào để xử lý việc lưu trữ hay gửi thông tin xác thực lên server. Trạng thái (đã login hay chưa) được lưu trên RAM của Server.

**2. Dùng Token (Thường dùng cho REST API)**
* **Mô hình:** Backend chỉ viết API (trả về JSON). Frontend là một ứng dụng tách biệt hoàn toàn (React, Vue, Angular, hoặc App Mobile).
* **Hoạt động:** Server không lưu trữ gì cả (Stateless). Khi login, server cấp 1 chuỗi Token (VD: JWT). Frontend phải **tự viết code** để lưu Token này (vào Local Storage), và cũng phải **tự viết code** để nhét Token đó vào Header của từng request mỗi khi gọi API.
* **Đặc điểm:** Tiết kiệm RAM cho Server (vì không cần lưu Session), dễ dàng Scale (mở rộng nhiều server).

Vì ứng dụng bạn đang làm trả về view (như `"listproduct"`) nên Spring Security đang rải thảm đỏ, dùng Session để hỗ trợ bạn tối đa mà không cần cấu hình phức tạp!


**Không, mặc định thì bạn không cần phải tạo thêm bảng nào dưới Database cả!**

Dưới đây là lý do tại sao và nó hoạt động như thế nào:

**1. Mặc định Session được lưu ở đâu?**
Theo thiết lập mặc định của Spring Boot (nó dùng Tomcat làm server nhúng), toàn bộ Session sẽ được lưu cục bộ trên **bộ nhớ RAM (In-Memory)** của server. 
Nghĩa là Tomcat tự tạo ra một vùng nhớ để quản lý các "phiên làm việc" này. Việc này không chạm gì đến Database của bạn (như MySQL hay PostgreSQL) cả.

**2. Điểm yếu của lưu Session trên RAM là gì?**
Vì dữ liệu nằm trên RAM, nên mỗi khi bạn **khởi động lại Server** (stop/start lại ứng dụng Spring Boot để test code mới), bộ nhớ RAM được giải phóng. Kết quả là mọi Session bị xóa sạch $\rightarrow$ Tất cả các tài khoản đang đăng nhập trên trình duyệt sẽ đồng loạt bị **đăng xuất (văng ra khỏi hệ thống)**.
*(Đây là lý do đôi khi bạn đang code, reload lại server xong qua trình duyệt F5 là lại bị đá về trang Login).*

**3. Vậy khi nào thì người ta mới lưu Session xuống Database?**
Trong các hệ thống thực tế (Production) lớn, người ta mới cần lưu Session ra ngoài RAM (xuống Database hoặc Cache như Redis) khi:
* **Không muốn user bị rớt mạng/đăng xuất** mỗi lúc bảo trì hay khởi động lại Server.
* **Hệ thống có nhiều Server:** Chẳng hạn bạn chạy 3 server Spring Boot cùng lúc. Nếu user đăng nhập ở Server A, nhưng khi bấm nút thì đường truyền đẩy qua Server B. Nếu không có một cái Database (hoặc Redis) chung để check session, Server B sẽ không nhận ra user đó là ai.

*(Nếu bạn thực sự muốn lưu Session xuống DB sau này, Spring có một công cụ tên là **Spring Session JDBC**. Bạn chỉ cần thêm thư viện đó vào, nó sẽ **tự động** script ra 2-3 cái bảng (như `SPRING_SESSION`, `SPRING_SESSION_ATTRIBUTES`) vào thẳng DB của bạn mà bạn chả cần phải tự viết mã SQL).*

**Tóm lại cho hiện tại:** 
Với project Trello Mini của bạn lúc này đang làm, bạn cứ dùng kiểu mặc định là nhanh, gọn và nhàn nhất. **Không cần đụng chạm gì dưới Database cả nhé!** Việc bị đăng xuất mỗi khi tắt/bật lại code là chuyện hoàn toàn bình thường khi dev.