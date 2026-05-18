package com.example.Trello_Mini.service.Task;
import com.example.Trello_Mini.dto.request.Task.TaskCreationRequest;
import com.example.Trello_Mini.dto.request.Task.TaskUpdateRequest;
import com.example.Trello_Mini.dto.response.Task.TaskResponse;
import java.util.List;
public interface TaskService {
    TaskResponse createTask(TaskCreationRequest request);
    List<TaskResponse> getTasksByBoard(Long boardId);
    TaskResponse getTask(Long taskId);
    TaskResponse updateTask(Long taskId, TaskUpdateRequest request);
    void deleteTask(Long taskId);
}
