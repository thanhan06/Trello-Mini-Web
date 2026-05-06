package com.example.Trello_Mini.repository.Task;

import com.example.Trello_Mini.entity.Task.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByBoardId(Long boardId);
}

