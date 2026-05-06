package com.example.Trello_Mini.repository.UserWorkspace;

import com.example.Trello_Mini.entity.UserWorkspace.UserWorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserWorkspaceRepository extends JpaRepository<UserWorkspaceEntity, Long> {
    Optional<UserWorkspaceEntity> findByUserIdAndWorkspaceId(UUID userId, Long workspaceId);
}
