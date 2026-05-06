package com.example.Trello_Mini.repository.WorkSpace;

import com.example.Trello_Mini.entity.WorkSpace.WorkSpaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkSpaceRepository extends JpaRepository<WorkSpaceEntity, Long> {
    Optional<WorkSpaceEntity> findById(Long aLong);
}
