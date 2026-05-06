package com.example.Trello_Mini.controller.User;

import com.example.Trello_Mini.common.ApiResponse;
import com.example.Trello_Mini.common.ApiResponses;
import com.example.Trello_Mini.dto.request.WorkSpaceCreationRequest;
import com.example.Trello_Mini.dto.response.WorkSpaceResponse;
import com.example.Trello_Mini.service.WorkSpace.WorkSpaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class WorkSpaceController {
    WorkSpaceService workSpaceService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<WorkSpaceResponse>> createWorkSpace(@RequestBody WorkSpaceCreationRequest request,  HttpServletRequest httpReq) {
        // Implement the logic to create a workspace and return the response
        return ApiResponses.ok(httpReq, workSpaceService.createWorkSpace(request));
    }
}
