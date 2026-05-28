package com.example.Trello_Mini.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SercurityConfig {
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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/", "/index", "/index.html").permitAll()
                        // Xóa /shop/product-list khỏi permitAll() để bắt buộc check Token
                        .requestMatchers(HttpMethod.GET, "/shop/product-order").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/test/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/productlist").permitAll()
                        .requestMatchers(HttpMethod.GET, "/shop/products", "/shop/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/shop/product-types", "/shop/product-types/**").permitAll()
                        .requestMatchers("/bootstrap/**", "/css/**", "/js/**", "/img/**", "/favicon.ico", "/uploads/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/images/upload").permitAll() // Cho phép upload file
                                                                                            // (test)
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(bearerTokenResolver()) // Đưa bộ giải mã token tùy chỉnh vào đây
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                // ✅ Thêm cả ở đây để bắt các lỗi auth khác
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

        return http.build();
    }

    // Bean này giúp Spring Security dọc Token từ Cookie nếu tải trang bằng chuẩn
    // HTML GET (không có header Bearer)
    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver defaultResolver = new DefaultBearerTokenResolver();
        return request -> {
            // 1. Thử header trước
            String token = defaultResolver.resolve(request);
            if (token != null) {
                return isValidJwtFormat(token) ? token : null;
            }
            // 2. Thử cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        String value = cookie.getValue();
                        // ✅ Kiểm tra format JWT trước khi trả về
                        return isValidJwtFormat(value) ? value : null;
                    }
                }
            }
            return null;
        };
    }

    // JWT luôn có đúng 3 phần cách nhau bởi dấu "."
    private boolean isValidJwtFormat(String token) {
        if (token == null)
            return false;
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
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
}
