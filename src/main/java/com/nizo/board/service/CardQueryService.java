package com.nizo.board.service;

import com.nizo.board.dto.CardDetailsDTO;
import com.nizo.board.persistence.dao.BoardColumnDAO;
import com.nizo.board.persistence.dao.CardDAO;
import com.nizo.board.persistence.entity.BoardColumnEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService{
    private final Connection connection;

    public Optional<CardDetailsDTO> getById(Long id) throws SQLException{
        var dao = new CardDAO(connection);
        return dao.findById(id);
    }
}
