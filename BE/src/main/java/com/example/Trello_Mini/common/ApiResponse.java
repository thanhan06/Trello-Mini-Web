package com.example.Trello_Mini.common;

import java.time.Instant;

public record ApiResponse<T>(Instant timestamp, int status, String message, String path, T data) {}
