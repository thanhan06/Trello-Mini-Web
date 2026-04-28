package com.example.Trello_Mini.config;

import java.io.IOException;
import java.time.Instant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.example.Trello_Mini.common.ApiError;
import com.example.Trello_Mini.common.ErrorCode;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        response.setStatus(errorCode.status().value());
        response.setContentType("application/json;charset=UTF-8");

        ApiError apiError = new ApiError(
                Instant.now(),
                errorCode.status().value(),
                errorCode.code(),
                errorCode.message(),
                request.getRequestURI());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule for Instant
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Write as ISO-8601 string

        response.getWriter().write(objectMapper.writeValueAsString(apiError));
        response.flushBuffer();
    }
}
