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








#################################################################################

Bạn đang gặp vấn đề gì cụ thể? Mô tả thêm để mình hỗ trợ chính xác hơn nhé.

Nhưng mình đoán vấn đề phổ biến khi dùng **fetch JS + Spring Security + `@AuthenticationPrincipal`** trong mô hình MVC/Thymeleaf là:

---

## Vấn đề thường gặp

Khi bạn dùng `fetch()` để gọi API login thay vì submit form HTML thông thường, **Spring Security không tự động tạo session** cho browser — dẫn đến `@AuthenticationPrincipal` trả về `null` ở các request tiếp theo.

---

## Nguyên nhân

Spring Security mặc định dùng **form-based login** với redirect. Khi bạn dùng `fetch`, response là JSON/redirect nhưng **cookie session không được lưu đúng cách**, hoặc request không kèm `credentials`.

---

## Giải pháp

### 1. Đảm bảo fetch gửi kèm credentials (cookie)

```js
fetch('/login', {
  method: 'POST',
  credentials: 'include', // ← quan trọng!
  headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  body: new URLSearchParams({ username: '...', password: '...' })
})
```

---

### 2. Cấu hình Spring Security cho phép CORS + session

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // hoặc config CSRF token đúng cách
        .formLogin(form -> form
            .loginProcessingUrl("/login")
            .successHandler((req, res, auth) -> {
                res.setStatus(200); // trả 200 thay vì redirect
            })
            .failureHandler((req, res, ex) -> {
                res.setStatus(401);
            })
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        );
    return http.build();
}
```

---

### 3. Dùng `@AuthenticationPrincipal` đúng cách

```java
@GetMapping("/dashboard")
public String dashboard(@AuthenticationPrincipal UserDetails user, Model model) {
    if (user == null) return "redirect:/login"; // chưa login
    model.addAttribute("username", user.getUsername());
    return "dashboard";
}
```

---

Bạn có thể mô tả lỗi cụ thể hơn không — ví dụ `null`, `403`, hay redirect không mong muốn? Mình sẽ hỗ trợ chính xác hơn.










#################################################################################

Lỗi này thường do **Spring Security mặc định xử lý form login** chỉ nhận `application/x-www-form-urlencoded` từ form submit thật, nhưng khi dùng `fetch` có thể bị chặn bởi **CSRF token** hoặc **Content-Type không khớp**.

---

## Cách fix nhanh nhất

### Option 1: Gửi đúng format + kèm CSRF token

```js
// Lấy CSRF token từ thẻ meta trong HTML (Thymeleaf tự render)
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

fetch('/login', {
  method: 'POST',
  credentials: 'include',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
    [csrfHeader]: csrfToken  // ví dụ: 'X-CSRF-TOKEN': 'abc123'
  },
  body: new URLSearchParams({
    username: 'youruser',
    password: 'yourpass'
  })
})
```

Thêm vào thẻ `<head>` trong layout Thymeleaf:

```html
<meta name="_csrf" th:content="${_csrf.token}"/>
<meta name="_csrf_header" th:content="${_csrf.headerName}"/>
```

---

### Option 2: Disable CSRF (chỉ dùng khi dev/test)

```java
http.csrf(csrf -> csrf.disable())
```

---

### Option 3: Dùng form submit thật thay vì fetch

Nếu bạn không cần xử lý response bằng JS, đơn giản nhất là dùng form HTML — Spring Security xử lý hoàn toàn tự động:

```html
<form th:action="@{/login}" method="post">
    <input type="text" name="username"/>
    <input type="password" name="password"/>
    <button type="submit">Đăng nhập</button>
</form>
```

---

Bạn đang dùng CSRF enable hay disable, và lý do cần dùng `fetch` thay vì form submit là gì? Mình sẽ gợi ý hướng phù hợp nhất.


#################################################################################
Nếu đã disable CSRF thì vấn đề thường là **Content-Type không được Spring Security chấp nhận** khi gửi qua `fetch`. Thử như sau:

---

## Fix: Gửi đúng Content-Type + credentials

```js
fetch('/login', {
  method: 'POST',
  credentials: 'include',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  body: new URLSearchParams({
    username: document.getElementById('username').value,
    password: document.getElementById('password').value,
  }).toString()
})
.then(res => {
  if (res.ok || res.redirected) {
    window.location.href = '/dashboard'; // redirect thủ công
  } else {
    console.log('Login thất bại', res.status);
  }
})
```

---

## Kiểm tra lại Security Config

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")  // ← URL nhận POST
            .defaultSuccessUrl("/dashboard", true)
            .permitAll()
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/css/**", "/js/**").permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
}
```

---

## Lưu ý quan trọng

Khi Spring Security xử lý login thành công, nó **redirect 302** — `fetch` sẽ follow redirect tự động và `res.ok` có thể là `true` nhưng `res.redirected` sẽ là `true`.

Bạn thử log thêm để debug:

```js
.then(res => {
  console.log('status:', res.status);
  console.log('redirected:', res.redirected);
  console.log('url:', res.url);
})
```

---

Bạn đang thấy lỗi cụ thể ở đâu — console trình duyệt hay log Spring Boot?












#################################################################################
Vì **Spring Security's form login chỉ đọc username/password từ request parameter**, không parse JSON body.

---

## Cụ thể bên trong Spring Security

Khi bạn POST `/login`, Spring Security dùng class **`UsernamePasswordAuthenticationFilter`**, nó lấy thông tin như sau:

```java
// Bên trong Spring Security - bạn không sửa được trực tiếp
String username = request.getParameter("username");
String password = request.getParameter("password");
```

`request.getParameter()` **chỉ đọc được** từ:
- `application/x-www-form-urlencoded`
- `multipart/form-data`

**Không đọc được** từ `application/json` vì JSON nằm trong **request body stream**, không phải parameter.

---

## Nếu muốn dùng `application/json`

Bạn phải **tự viết custom filter** thay thế:

```java
public class JsonLoginFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                 HttpServletResponse response) {
        try {
            // Đọc JSON body thủ công
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(request.getInputStream());

            String username = json.get("username").asText();
            String password = json.get("password").asText();

            UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(username, password);

            return getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

Rồi đăng ký vào Security Config:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    JsonLoginFilter jsonFilter = new JsonLoginFilter();
    jsonFilter.setAuthenticationManager(authenticationManager(http));
    jsonFilter.setFilterProcessesUrl("/login");

    http
        .csrf(csrf -> csrf.disable())
        .addFilterAt(jsonFilter, UsernamePasswordAuthenticationFilter.class)
        ...
    return http.build();
}
```

---

## Tóm lại

| | `x-www-form-urlencoded` | `application/json` |
|---|---|---|
| Spring Security mặc định | ✅ Hoạt động ngay | ❌ Cần custom filter |
| Độ phức tạp | Thấp | Cao hơn |
| Phù hợp MVC + Thymeleaf | ✅ | Overkill nếu không làm REST API |

Vì bạn đang dùng **MVC + Thymeleaf** (không phải REST API thuần), dùng `x-www-form-urlencoded` là hướng đúng và đơn giản nhất. Bạn có muốn mình giúp debug tiếp lỗi hiện tại không?






#################################################################################
## Tất cả điểm có thể gây lỗi khi dùng fetch + Spring Security

---

### 1. Thiếu `credentials: 'include'`

**Vấn đề:** Browser không gửi cookie session → server không nhận ra user đã login.

```js
// ❌ Sai
fetch('/login', { method: 'POST', ... })

// ✅ Đúng
fetch('/login', { method: 'POST', credentials: 'include', ... })
```

---

### 2. Sai `loginProcessingUrl`

**Vấn đề:** Fetch gọi sai URL, Spring Security không bắt được request.

```java
// Config
.formLogin(form -> form
    .loginProcessingUrl("/login") // ← fetch phải POST đúng URL này
)
```

```js
// ❌ Sai URL
fetch('/api/login', ...)

// ✅ Phải khớp với loginProcessingUrl
fetch('/login', ...)
```

---

### 3. Sai tên field username/password

**Vấn đề:** Spring Security mặc định đọc field tên `username` và `password`. Nếu gửi tên khác sẽ null.

```js
// ❌ Sai tên field
body: new URLSearchParams({ user: '...', pass: '...' })

// ✅ Đúng tên mặc định
body: new URLSearchParams({ username: '...', password: '...' })
```

Hoặc nếu muốn đổi tên field thì khai báo trong config:

```java
.formLogin(form -> form
    .usernameParameter("email")   // đổi tên field
    .passwordParameter("passwd")
)
```

---

### 4. Không xử lý redirect 302

**Vấn đề:** Spring Security trả về 302 redirect sau login thành công, `fetch` follow redirect tự động nhưng không báo lỗi rõ ràng.

```js
fetch('/login', {
  method: 'POST',
  credentials: 'include',
  headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  body: new URLSearchParams({ username, password })
})
.then(res => {
  // ✅ Kiểm tra redirected thay vì chỉ res.ok
  if (res.ok || res.redirected) {
    window.location.href = '/dashboard';
  } else {
    // Login thất bại
  }
})
```

---

### 5. CSRF chưa thật sự disable

**Vấn đề:** Nghĩ đã disable nhưng config không được apply.

```java
// ❌ Có thể không hoạt động đúng trong một số version
http.csrf().disable();

// ✅ Lambda style (Spring Boot 3+)
http.csrf(csrf -> csrf.disable())
```

---

### 6. Thiếu `permitAll()` cho `/login`

**Vấn đề:** Chính URL login bị chặn bởi Spring Security.

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/login", "/css/**", "/js/**").permitAll() // ← bắt buộc
    .anyRequest().authenticated()
)
```

---

### 7. `successHandler` redirect không tương thích với fetch

**Vấn đề:** Nếu custom `successHandler` dùng `sendRedirect`, fetch nhận redirect nhưng không điều hướng browser.

```java
// ❌ Dùng redirect trong successHandler khi login bằng fetch
.successHandler((req, res, auth) -> {
    res.sendRedirect("/dashboard"); // fetch không tự redirect browser
})

// ✅ Trả status 200, để JS tự redirect
.successHandler((req, res, auth) -> {
    res.setStatus(HttpServletResponse.SC_OK);
    res.getWriter().write("{\"success\": true}");
})
```

```js
.then(res => {
  if (res.ok) window.location.href = '/dashboard';
})
```

---

### 8. `failureHandler` không trả lỗi rõ ràng

**Vấn đề:** Login fail nhưng không biết lý do.

```java
// ✅ Trả status 401 + message rõ ràng
.failureHandler((req, res, ex) -> {
    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    res.setContentType("application/json");
    res.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
})
```

```js
.then(async res => {
  if (!res.ok) {
    const err = await res.json();
    console.log('Lỗi:', err.error);
  }
})
```

---

### 9. Session không được tạo

**Vấn đề:** Config `STATELESS` khiến server không lưu session → `@AuthenticationPrincipal` luôn null.

```java
// ❌ STATELESS không có session
.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

// ✅ Dùng IF_REQUIRED (mặc định) cho MVC
.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
```

---

### 10. Static files (JS/CSS) bị chặn

**Vấn đề:** File JS chứa code fetch bị 403, login không chạy được.

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
    .requestMatchers("/login").permitAll()
    .anyRequest().authenticated()
)
```

---

## Checklist tổng hợp

| # | Điểm kiểm tra | Trạng thái |
|---|---|---|
| 1 | `credentials: 'include'` trong fetch | ☐ |
| 2 | URL fetch khớp `loginProcessingUrl` | ☐ |
| 3 | Tên field `username` / `password` đúng | ☐ |
| 4 | Xử lý redirect 302 trong JS | ☐ |
| 5 | CSRF disable đúng cú pháp | ☐ |
| 6 | `/login` có `permitAll()` | ☐ |
| 7 | `successHandler` trả 200 thay vì redirect | ☐ |
| 8 | `failureHandler` trả 401 + message | ☐ |
| 9 | Session policy là `IF_REQUIRED` | ☐ |
| 10 | Static files được permit | ☐ |

Bạn đang bị lỗi ở bước nào, mình debug cùng nhé?




#################################################################################
Cái này nằm trong file **Security Config** của bạn, thường là:

```
src/main/java/com/yourproject/config/SecurityConfig.java
```

---

## Ví dụ đầy đủ file SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form
                .loginPage("/login")                    // trang hiển thị form login
                .loginProcessingUrl("/login")           // URL nhận POST từ fetch
                .successHandler((req, res, auth) -> {   // ← chỗ này
                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"success\": true}");
                })
                .failureHandler((req, res, ex) -> {     // ← và chỗ này
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\": \"Sai tài khoản hoặc mật khẩu\"}");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll()
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
```

---

## Phía JS gọi tương ứng

```js
fetch('/login', {
  method: 'POST',
  credentials: 'include',
  headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  body: new URLSearchParams({
    username: document.getElementById('username').value,
    password: document.getElementById('password').value,
  })
})
.then(async res => {
  if (res.ok) {
    window.location.href = '/dashboard'; // JS tự redirect
  } else {
    const err = await res.json();
    alert(err.error); // hiện thông báo lỗi
  }
})
```

Bạn đang có file SecurityConfig chưa hay cần tạo mới?