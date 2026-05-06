package com.example.Trello_Mini.service.Task;

import com.example.Trello_Mini.dto.request.Task.TaskCreationRequest;
import com.example.Trello_Mini.dto.request.Task.TaskUpdateRequest;
import com.example.Trello_Mini.dto.response.Task.TaskResponse;
import com.example.Trello_Mini.common.ApiException;
import com.example.Trello_Mini.common.ErrorCode;
import com.example.Trello_Mini.entity.Board.BoardEntity;
import com.example.Trello_Mini.entity.Task.TaskEntity;
import com.example.Trello_Mini.entity.User.UserEntity;
import com.example.Trello_Mini.mapper.Task.TaskMapper;
import com.example.Trello_Mini.repository.Board.BoardRepository;
import com.example.Trello_Mini.repository.Task.TaskRepository;
import com.example.Trello_Mini.repository.User.UserRepository;
import com.example.Trello_Mini.repository.UserWorkspace.UserWorkspaceRepository;
import com.example.Trello_Mini.entity.UserWorkspace.UserWorkspaceEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskService {

    TaskRepository taskRepository;
    BoardRepository boardRepository;
    UserRepository userRepository;
    UserWorkspaceRepository userWorkspaceRepository;
    TaskMapper taskMapper;

    public TaskResponse createTask(TaskCreationRequest request) {
        BoardEntity board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        UserWorkspaceEntity userWorkspace = userWorkspaceRepository.findByUserIdAndWorkspaceId(currentUser.getId(), board.getWorkspace().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED));
                
        if (!"OWNER".equalsIgnoreCase(userWorkspace.getRole())) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        UserEntity assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        }

        TaskEntity taskEntity = taskMapper.toTaskEntity(request);
        if (taskEntity.getStatus() == null || taskEntity.getStatus().trim().isEmpty()) {
            taskEntity.setStatus("TODO");
        }
        taskEntity.setBoard(board);
        taskEntity.setAssignedTo(assignedTo);

        return taskMapper.toTaskResponse(taskRepository.save(taskEntity));
    }

    public List<TaskResponse> getTasksByBoard(Long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new ApiException(ErrorCode.BOARD_NOT_FOUND);
        }
        return taskRepository.findByBoardId(boardId).stream()
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getTask(Long taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException(ErrorCode.TASK_NOT_FOUND));
        return taskMapper.toTaskResponse(task);
    }

    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException(ErrorCode.TASK_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        UserWorkspaceEntity userWorkspace = userWorkspaceRepository.findByUserIdAndWorkspaceId(currentUser.getId(), task.getBoard().getWorkspace().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED));

        boolean isOwner = "OWNER".equalsIgnoreCase(userWorkspace.getRole());
        boolean isAssignedUser = task.getAssignedTo() != null && task.getAssignedTo().getId().equals(currentUser.getId());

        // Check if updating only status
        boolean isOnlyUpdatingStatus = request.getTitle() == null && request.getDescription() == null && request.getAssignedToId() == null;

        if (!isOwner) {
            if (isOnlyUpdatingStatus && isAssignedUser) {
                // Allowed to update status
            } else {
                throw new ApiException(ErrorCode.UNAUTHORIZED);
            }
        }

        taskMapper.updateTaskEntity(task, request);

        if (request.getAssignedToId() != null) {
             UserEntity assignedTo = userRepository.findById(request.getAssignedToId())
                     .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
             task.setAssignedTo(assignedTo);
        } // For this basic fix we assume null assignedToId means don't update assignedTo. If we wanted to remove assignment, we might approach it differently. Let's just fix the compilation.

        return taskMapper.toTaskResponse(taskRepository.save(task));
    }

    public void deleteTask(Long taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ApiException(ErrorCode.TASK_NOT_FOUND));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        UserWorkspaceEntity userWorkspace = userWorkspaceRepository.findByUserIdAndWorkspaceId(currentUser.getId(), task.getBoard().getWorkspace().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.UNAUTHORIZED));

        if (!"OWNER".equalsIgnoreCase(userWorkspace.getRole())) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        taskRepository.deleteById(taskId);
    }
}
