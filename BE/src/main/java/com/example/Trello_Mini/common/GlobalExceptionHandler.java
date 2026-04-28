package com.example.Trello_Mini.common;

import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex, HttpServletRequest req) {
        ErrorCode ec = ex.getErrorCode();
        return ResponseEntity.status(ec.status())
                .body(new ApiError(Instant.now(), ec.status().value(), ec.code(), ec.message(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String enumKey = ex.getFieldError() != null ? ex.getFieldError().getDefaultMessage() : null;
        ErrorCode ec = ErrorCode.COMMON_VALIDATION_FAILED;
        String message = ec.message();

        try {
            if (enumKey != null) {
                ec = ErrorCode.valueOf(enumKey);
                message = ec.message();

                try {
                    ConstraintViolation<?> violation =
                            ex.getBindingResult().getAllErrors().get(0).unwrap(ConstraintViolation.class);
                    message = mapAttribute(message, violation);
                } catch (Exception ignored) {
                    // Nếu lỗi không phải từ Custom Validation thì bỏ qua
                }
            }
        } catch (IllegalArgumentException e) {
            // Không tìm thấy trong enum thì giữ mặc định
        }

        return ResponseEntity.status(ec.status())
                .body(new ApiError(Instant.now(), ec.status().value(), ec.code(), message, req.getRequestURI()));
    }

    private String mapAttribute(String message, ConstraintViolation<?> violation) {
        var attributes = violation.getConstraintDescriptor().getAttributes();
        if (attributes != null) {
            for (var entry : attributes.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                if (message.contains(placeholder)) {
                    message = message.replace(placeholder, String.valueOf(entry.getValue()));
                }
            }
        }
        return message;
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        ErrorCode ec = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(ec.status())
                .body(new ApiError(Instant.now(), ec.status().value(), ec.code(), ec.message(), req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnknown(Exception ex, HttpServletRequest req) {
        ex.printStackTrace(); // Log the actual error to console
        ErrorCode ec = ErrorCode.COMMON_INTERNAL_ERROR;
        return ResponseEntity.status(ec.status())
                .body(new ApiError(Instant.now(), ec.status().value(), ec.code(), ec.message(), req.getRequestURI()));
    }
}
