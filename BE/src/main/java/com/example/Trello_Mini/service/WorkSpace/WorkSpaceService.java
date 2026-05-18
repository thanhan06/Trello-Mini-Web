package com.example.Trello_Mini.service.WorkSpace;
import com.example.Trello_Mini.dto.request.WorkSpaceCreationRequest;
import com.example.Trello_Mini.dto.response.WorkSpaceResponse;
public interface WorkSpaceService {
    WorkSpaceResponse createWorkSpace(WorkSpaceCreationRequest request);
}
