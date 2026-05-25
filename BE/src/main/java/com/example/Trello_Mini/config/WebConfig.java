package com.example.Trello_Mini.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ URL /uploads/** vào thư mục vật lý uploads/ ngoài root của project BE
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
