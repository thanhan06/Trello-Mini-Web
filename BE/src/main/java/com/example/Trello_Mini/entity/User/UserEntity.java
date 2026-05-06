package com.example.Trello_Mini.entity.User;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import com.example.Trello_Mini.entity.UserWorkspace.UserWorkspaceEntity;
import com.example.Trello_Mini.entity.Task.TaskEntity;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    String password;

    @Column(nullable = false)
    String role;

    LocalDate dob;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<UserWorkspaceEntity> userWorkspaces;

    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL)
    Set<TaskEntity> tasks;
}
