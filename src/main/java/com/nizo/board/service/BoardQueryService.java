package com.nizo.board.service;

import com.nizo.board.dto.BoardDetailsDTO;
import com.nizo.board.persistence.dao.BoardColumnDAO;
import com.nizo.board.persistence.dao.BoardDAO;
import com.nizo.board.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService{
    private final Connection connection;
    public Optional<BoardEntity> getBoardById(Long id) throws SQLException{
        var dao = new BoardDAO(connection);
        var boardColumnDao = new BoardColumnDAO(connection);
        var optionalBoard = dao.findById(id);
        if (optionalBoard.isPresent()){
            var entity = optionalBoard.get();
            entity.setBoardColumns(boardColumnDao.findByBoardId(entity.getId()));
            return Optional.of(entity);
        };
        return Optional.empty();
    }
    public Optional<BoardDetailsDTO> getBoardByIdWithDetails(Long id) throws SQLException{
        var dao = new BoardDAO(connection);
        var boardColumnDao = new BoardColumnDAO(connection);
        var optionalBoard = dao.findById(id);
        if (optionalBoard.isPresent()){
            var entity = optionalBoard.get();
            var columns = boardColumnDao.findByIdWithDetails(entity.getId());
            return Optional.of(new BoardDetailsDTO(entity.getId(), entity.getName(), columns));
        };
        return Optional.empty();
    }

}
