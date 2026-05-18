package com.example.Trello_Mini.service.Board;
import com.example.Trello_Mini.dto.request.Board.BoardCreationRequest;
import com.example.Trello_Mini.dto.response.Board.BoardResponse;
public interface BoardService {
    BoardResponse createBoard(BoardCreationRequest request);
}
