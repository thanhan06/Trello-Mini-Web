package com.example.Trello_Mini.repository.Board;

import com.example.Trello_Mini.entity.Board.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
}

