package com.nizo.board.service;

import com.nizo.board.persistence.dao.BoardColumnDAO;
import com.nizo.board.persistence.entity.BoardColumnEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardColumnQueryService{
    private final Connection connection;

    public Optional<BoardColumnEntity> getById(Long id) throws SQLException{
        var dao = new BoardColumnDAO(connection);
        return dao.findById(id);
    }
}
