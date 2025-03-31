package com.nizo.board.service;

import com.nizo.board.persistence.dao.BoardColumnDAO;
import com.nizo.board.persistence.dao.BoardDAO;
import com.nizo.board.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService{
    private final Connection connection;
    public boolean deleteBoardById (Long id) throws SQLException{
        var dao = new BoardDAO(connection);
        try{
            var boardExists = dao.boardExists(id);
            if(!boardExists){
                return false;
            }
            dao.deleteById(id);
            connection.commit();
            return true;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
    public void createBoard(BoardEntity entity) throws SQLException{
        var dao = new BoardDAO(connection);
        var boardColumnDao = new BoardColumnDAO(connection);
        try {
            dao.save(entity);
            var columns = entity.getBoardColumns().stream().peek(c -> c.setBoard(entity)).toList();
            for(var col : columns){
                boardColumnDao.save(col);
            }
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
}
