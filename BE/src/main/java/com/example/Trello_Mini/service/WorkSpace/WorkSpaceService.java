package com.example.Trello_Mini.service.WorkSpace;

import com.example.Trello_Mini.dto.request.WorkSpaceCreationRequest;
import com.example.Trello_Mini.dto.response.WorkSpaceResponse;
import com.example.Trello_Mini.entity.User.UserEntity;
import com.example.Trello_Mini.entity.UserWorkspace.UserWorkspaceEntity;
import com.example.Trello_Mini.entity.WorkSpace.WorkSpaceEntity;
import com.example.Trello_Mini.mapper.WorkSpace.WorkSpaceMapper;
import com.example.Trello_Mini.repository.User.UserRepository;
import com.example.Trello_Mini.repository.UserWorkspace.UserWorkspaceRepository;
import com.example.Trello_Mini.repository.WorkSpace.WorkSpaceRepository;
import com.example.Trello_Mini.common.ApiException;
import com.example.Trello_Mini.common.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkSpaceService {
    WorkSpaceRepository workSpaceRepository;
    WorkSpaceMapper workSpaceMapper;
    UserRepository userRepository;
    UserWorkspaceRepository userWorkspaceRepository;
    
    public WorkSpaceResponse createWorkSpace(WorkSpaceCreationRequest request){
        // Lấy thông tin user hiện tại đang đăng nhập
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // Tạo Workspace
        WorkSpaceEntity workSpace = workSpaceMapper.toWorkSpaceEntity(request);
        WorkSpaceEntity savedWorkSpace = workSpaceRepository.save(workSpace);

        // Gán Workspace này cho người vừa tạo với quyền là OWNER qua bảng trung gian
        UserWorkspaceEntity userWorkspace = UserWorkspaceEntity.builder()
                .user(user)
                .workspace(savedWorkSpace)
                .role("OWNER")
                .build();
        userWorkspaceRepository.save(userWorkspace);

        return workSpaceMapper.toWorkSpaceResponse(savedWorkSpace);
    }    
    
}
