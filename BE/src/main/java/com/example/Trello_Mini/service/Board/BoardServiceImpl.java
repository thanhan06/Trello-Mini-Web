package com.example.Trello_Mini.service.Board;

import com.example.Trello_Mini.common.ApiException;
import com.example.Trello_Mini.common.ErrorCode;
import com.example.Trello_Mini.dto.request.Board.BoardCreationRequest;
import com.example.Trello_Mini.dto.response.Board.BoardResponse;
import com.example.Trello_Mini.entity.Board.BoardEntity;
import com.example.Trello_Mini.entity.WorkSpace.WorkSpaceEntity;
import com.example.Trello_Mini.mapper.Board.BoardMapper;
import com.example.Trello_Mini.repository.Board.BoardRepository;
import com.example.Trello_Mini.repository.WorkSpace.WorkSpaceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service("boardService")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BoardServiceImpl implements BoardService {
    BoardRepository boardRepository;
    WorkSpaceRepository workSpaceRepository;
    BoardMapper boardMapper;

    public BoardResponse createBoard(BoardCreationRequest request) {
        // Find workspace
        WorkSpaceEntity workspace = workSpaceRepository.findById(request.getWorkspaceId())
                .orElseThrow(() -> new ApiException(ErrorCode.WORKSPACE_NOT_FOUND));

        // Create board and link to workspace
        BoardEntity boardEntity = boardMapper.toBoardEntity(request, workspace);
        BoardEntity savedBoard = boardRepository.save(boardEntity);

        return boardMapper.toBoardResponse(savedBoard);
    }
}

