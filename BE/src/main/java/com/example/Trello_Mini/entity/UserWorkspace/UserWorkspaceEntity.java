package com.example.Trello_Mini.entity.UserWorkspace;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import com.example.Trello_Mini.entity.User.UserEntity;
import com.example.Trello_Mini.entity.WorkSpace.WorkSpaceEntity;

@Entity
@Table(name = "user_workspace")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserWorkspaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    WorkSpaceEntity workspace;

    @Column(nullable = false)
    String role; // Ví dụ: OWNER, MEMBER, GUEST... để phân quyền trong workspace
}
