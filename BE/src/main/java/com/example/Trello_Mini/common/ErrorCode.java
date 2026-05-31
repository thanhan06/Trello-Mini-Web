package com.example.Trello_Mini.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // COMMON
    COMMON_VALIDATION_FAILED("COMMON_VALIDATION_FAILED", HttpStatus.BAD_REQUEST, "Validation failed"),
    COMMON_BAD_REQUEST("COMMON_BAD_REQUEST", HttpStatus.BAD_REQUEST, "Bad request"),
    COMMON_INTERNAL_ERROR("COMMON_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"),

    // CATEGORY
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND, "Category not found"),
    CATEGORY_NAME_EXISTS("CATEGORY_NAME_EXISTS", HttpStatus.CONFLICT, "Category name already exists"),

    // WORKSPACE
    WORKSPACE_NOT_FOUND("WORKSPACE_NOT_FOUND", HttpStatus.NOT_FOUND, "Workspace not found"),

    // BOARD
    BOARD_NOT_FOUND("BOARD_NOT_FOUND", HttpStatus.NOT_FOUND, "Board not found"),

    // TASK
    TASK_NOT_FOUND("TASK_NOT_FOUND", HttpStatus.NOT_FOUND, "Task not found"),

    // PRODUCT
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND, "Product not found"),
    PRODUCT_NAME_EXISTS("PRODUCT_NAME_EXISTS", HttpStatus.CONFLICT, "Product name already exists"),
    PRODUCT_TYPE_NOT_FOUND("PRODUCT_TYPE_NOT_FOUND", HttpStatus.NOT_FOUND, "Product type not found"),

    // ORDER
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", HttpStatus.NOT_FOUND, "Order not found"),
    ORDER_ITEM_NOT_ENOUGH("ORDER_ITEM_NOT_ENOUGH", HttpStatus.CONFLICT, "Số lượng sản phẩm không đủ"),

    // USER / ROLE
    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "User not found"),
    MSTUSER_NOT_FOUND("MSTUSER_NOT_FOUND", HttpStatus.NOT_FOUND, "MstUser not found"),
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", HttpStatus.NOT_FOUND, "Role not found"),
    USER_ROLE_EXISTS("USER_ROLE_EXISTS", HttpStatus.CONFLICT, "User already has this role"),
    USER_ROLE_NOT_FOUND("USER_ROLE_NOT_FOUND", HttpStatus.NOT_FOUND, "User does not have this role"),
    USER_EMAIL_EXISTS("USER_EMAIL_EXISTS", HttpStatus.CONFLICT, "Email already exists"),
    // AUTH
    LOGIN_FAILED("LOGIN_FAILED", HttpStatus.UNAUTHORIZED, "Invalid email or password"),
    UNAUTHENTICATED("UNAUTHENTICATED", HttpStatus.UNAUTHORIZED, "Authentication required"),
    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.FORBIDDEN, "You do not have permission to access this resource"),

    // VALIDATION

    INVALID_DOB("INVALID_DOB", HttpStatus.BAD_REQUEST, "Your age must be at least {min} years old"),
    INVALID_PASSWORD(
            "INVALID_PASSWORD",
            HttpStatus.BAD_REQUEST,
            "Password must be at least {min} characters long and at most 100 characters long");
    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }
}
