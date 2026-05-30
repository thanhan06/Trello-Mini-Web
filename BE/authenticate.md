## Tổng kết toàn bộ

### Dependencies `pom.xml`

```xml
<!-- Core -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Security + JWT -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>

<!-- Thymeleaf + Security (dùng sec:authorize trong HTML) -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>

<!-- Jackson cho Instant / Java 8 Time -->
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

---

### Các file cần tạo

```
src/main/java/com/example/Trello_Mini/
│
├── config/
│   ├── SecurityConfig.java            ✅ có sẵn — sửa authorizeHttpRequests
│   ├── CustomJwtDecoder.java          ✅ có sẵn
│   └── JwtAuthenticationEntryPoint.java ✅ có sẵn — sửa redirect URL
│
├── common/
│   ├── ApiError.java                  🔧 cần tạo
│   └── ErrorCode.java                 🔧 cần tạo
│
├── dto/
│   ├── request/
│   │   └── IntrospectRequest.java     🔧 cần tạo
│   └── response/
│       └── IntrospectResponse.java    🔧 cần tạo
│
├── service/User/
│   └── AuthenticationService.java     🔧 cần tạo — có method introspect()
│
└── controller/
    └── ProductController.java         🔧 cần tạo

src/main/resources/
├── templates/
│   └── products.html                  🔧 cần tạo
└── application.properties             🔧 thêm jwt.signerKey
```

---

### Checklist theo thứ tự làm

**Bước 1 — Config**
- [ ] Thêm dependencies vào `pom.xml`
- [ ] Thêm `jwt.signerKey` vào `application.properties`

**Bước 2 — Common**
- [ ] Tạo `ApiError` record
- [ ] Tạo `ErrorCode` enum với `UNAUTHENTICATED`

**Bước 3 — DTO**
- [ ] Tạo `IntrospectRequest(String token)`
- [ ] Tạo `IntrospectResponse(boolean valid)`

**Bước 4 — Service**
- [ ] Tạo `AuthenticationService.introspect()` — verify signature + check blacklist + check expiry

**Bước 5 — Sửa SecurityConfig**
- [ ] Inject `CustomJwtDecoder`
- [ ] Sửa `authorizeHttpRequests` — chỉ bảo vệ `/products`
- [ ] Gắn `customJwtDecoder` vào `.jwt(jwt -> jwt.decoder(customJwtDecoder))`

**Bước 6 — Controller + View**
- [ ] Tạo `ProductController` với `@GetMapping("/products")`
- [ ] Tạo `templates/products.html`

---

### Luồng hoạt động cuối cùng

```
Trình duyệt GET /products
        ↓
BearerTokenResolver đọc cookie "accessToken"
        ↓
Không có cookie ──→ JwtAuthenticationEntryPoint ──→ redirect /auth/login
        ↓
Có cookie
        ↓
CustomJwtDecoder.decode(token)
  ├── blacklist / hết hạn ──→ redirect /auth/login
  └── hợp lệ
        ↓
ProductController → render products.html
```

@Bean
public JwtDecoder jwtDecoder() {
    SecretKeySpec key = new SecretKeySpec(
        signerKey.getBytes(), "HmacSHA512"
    );
    return NimbusJwtDecoder
        .withSecretKey(key)
        .macAlgorithm(MacAlgorithm.HS512)
        .build();
}


package com.example.Trello_Mini.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.Cookie;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.signerKey}")
    private String signerKey;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/users/register",
            "/auth/login",
            "/auth/google",
            "/auth/introspect",
            "/auth/logout",
            "/auth/refresh"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/bootstrap/**", "/css/**", "/js/**",
                    "/img/**", "/favicon.ico", "/uploads/**", "/images/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/", "/index", "/index.html"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers("/error").permitAll()
                // Protected
                .requestMatchers(HttpMethod.GET, "/products").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .bearerTokenResolver(bearerTokenResolver())
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");
        return NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver defaultResolver = new DefaultBearerTokenResolver();
        return request -> {
            // 1. Đọc từ Authorization header
            String token = defaultResolver.resolve(request);
            if (token != null) {
                return isValidJwtFormat(token) ? token : null;
            }
            // 2. Đọc từ cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        String value = cookie.getValue();
                        return isValidJwtFormat(value) ? value : null;
                    }
                }
            }
            return null;
        };
    }

    private boolean isValidJwtFormat(String token) {
        if (token == null) return false;
        return token.split("\\.").length == 3;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


Cả 2 class này đều nằm trong:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

Không cần cài thêm gì khác. Dependency này đã bao gồm toàn bộ những gì bạn dùng:
- `BearerTokenResolver`
- `DefaultBearerTokenResolver`
- `JwtDecoder`
- `NimbusJwtDecoder`
- `JwtAuthenticationConverter`
- `MacAlgorithm`