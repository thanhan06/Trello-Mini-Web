package com.example.Trello_Mini.controller.Task;

import com.example.Trello_Mini.common.ApiResponse;
import com.example.Trello_Mini.common.ApiResponses;
import com.example.Trello_Mini.dto.request.Task.TaskCreationRequest;
import com.example.Trello_Mini.dto.request.Task.TaskUpdateRequest;
import com.example.Trello_Mini.dto.response.Task.TaskResponse;
import com.example.Trello_Mini.service.Task.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {

    TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskCreationRequest request,
            HttpServletRequest httpReq) {
        return ApiResponses.created(httpReq, taskService.createTask(request));
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByBoard(
            @PathVariable Long boardId,
            HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, taskService.getTasksByBoard(boardId));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(
            @PathVariable Long taskId,
            HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, taskService.getTask(taskId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request,
            HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, taskService.updateTask(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long taskId,
            HttpServletRequest httpReq) {
        taskService.deleteTask(taskId);
        return ApiResponses.ok(httpReq, null);
    }
}
