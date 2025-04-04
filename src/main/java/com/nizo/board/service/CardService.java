package com.nizo.board.service;

import com.nizo.board.dto.BoardColumnInfoDto;
import com.nizo.board.dto.CardDetailsDTO;
import com.nizo.board.persistence.dao.BlockDAO;
import com.nizo.board.persistence.dao.CardDAO;
import com.nizo.board.persistence.entity.BoardColumnKind;
import com.nizo.board.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.nizo.board.persistence.entity.BoardColumnKind.CANCEL;
import static com.nizo.board.persistence.entity.BoardColumnKind.FINAL;

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

    public void moveToNextColumn(Long cardId, Long boardId,List<BoardColumnInfoDto> boardColumnsInfo) throws SQLException{
        try{
            var cardDao = new CardDAO(connection);
            var validCard = checkedCardCanBeMoved(cardId,boardId, boardColumnsInfo);
            cardDao.moveToColumn(validCard.id(), cardId);
            connection.commit();
        } catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void cancel (Long cardId, Long boardId, Long cancelColumnId , List<BoardColumnInfoDto> boardColumnsInfo) throws SQLException{
        try{
            var cardDao = new CardDAO(connection);
            var validCard = checkedCardCanBeMoved(cardId,boardId, boardColumnsInfo);
            cardDao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }

    public void block (Long cardId, Long boardId, String reason, List<BoardColumnInfoDto> boardColumnsInfo) throws SQLException{
        try{
            var cardDao = new CardDAO(connection);
            var cardDto = checkedCardExistsAndBlock(cardId,boardId);
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(cardDto.boadColumnId()))
                    .findFirst()
                    .orElseThrow();
            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                var message = "O card está em uma coluna do tipo %s e não pode ser bloqueado"
                        .formatted(currentColumn.kind());
                throw new IllegalStateException(message);
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.block(reason, cardId);
            connection.commit();
        } catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }
    public void unblock (Long cardId, Long boardId, String reason) throws SQLException{
        try{
            var cardDao = new CardDAO(connection);
            var cardExists = cardDao.findById(cardId, boardId);
            var dto = cardExists.orElseThrow(() -> new RuntimeException("O card de id %s não foi encontrado".formatted(cardId)));
            if(!dto.blocked()){
                throw new RuntimeException("O card %s não está bloqueado.".formatted(cardId));
            }
            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(reason, cardId);
            connection.commit();
        } catch (SQLException ex){
            connection.rollback();
            throw ex;
        }
    }
    private CardDetailsDTO checkedCardExistsAndBlock(Long cardId,Long boardId) throws SQLException{
        var cardDao = new CardDAO(connection);
        var cardExists = cardDao.findById(cardId, boardId);
        var dto = cardExists.orElseThrow(() -> new RuntimeException("O card de id %s não foi encontrado".formatted(cardId)));
        if(dto.blocked()){
            throw new RuntimeException("O card %s está bloqueado, é necessário desbloqueá-lo para mover".formatted(cardId));
        }
        return dto;
    }
    private BoardColumnInfoDto checkedCardCanBeMoved(Long cardId, Long boardId,List<BoardColumnInfoDto> boardColumnInfo) throws SQLException{
        var cardDto = this.checkedCardExistsAndBlock(cardId, boardId);
        var currentColumn = boardColumnInfo.stream().filter(bc -> bc.id().equals(cardDto.boadColumnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("O card informado pertence a outro board"));
        if(currentColumn.kind().equals(FINAL)){
            throw new IllegalArgumentException("O card já foi concluído");
        }
        return boardColumnInfo.stream().filter(bc -> bc.boardColumnOrder() == currentColumn.boardColumnOrder() + 1)
                .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado"));
    }
}
