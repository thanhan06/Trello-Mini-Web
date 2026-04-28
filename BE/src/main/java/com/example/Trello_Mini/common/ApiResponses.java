package com.example.Trello_Mini.common;

import java.net.URI;
import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ApiResponses {
    private ApiResponses() {}

    public static <T> ResponseEntity<ApiResponse<T>> ok(HttpServletRequest req, T data) {
        return build(req, HttpStatus.OK, "OK", data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(HttpServletRequest req, T data) {
        return build(req, HttpStatus.CREATED, "Created", data);
    }

    // NEW: created + Location header
    public static <T> ResponseEntity<ApiResponse<T>> created(HttpServletRequest req, URI location, T data) {
        ApiResponse<T> body =
                new ApiResponse<>(Instant.now(), HttpStatus.CREATED.value(), "Created", req.getRequestURI(), data);
        return ResponseEntity.created(location).body(body);
    }

    public static <T> ResponseEntity<ApiResponse<T>> build(
            HttpServletRequest req, HttpStatus status, String message, T data) {
        ApiResponse<T> body = new ApiResponse<>(Instant.now(), status.value(), message, req.getRequestURI(), data);
        return ResponseEntity.status(status).body(body);
    }
}
