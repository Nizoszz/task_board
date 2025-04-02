package com.nizo.board.service;

import com.nizo.board.persistence.dao.CardDAO;
import com.nizo.board.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class CardService{
    private final Connection connection;
    public CardEntity createCard(CardEntity entity) throws SQLException{
        try{
            var dao = new CardDAO(connection);
            dao.save(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }
}
