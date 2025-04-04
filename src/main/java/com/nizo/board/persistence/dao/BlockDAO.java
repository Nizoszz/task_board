package com.nizo.board.persistence.dao;

import lombok.AllArgsConstructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static com.nizo.board.persistence.util.OffDateTimeConverter.toTimestamp;

@AllArgsConstructor
public class BlockDAO{
    private final Connection connection;

    public void block(String reason,Long cardId)throws SQLException{
        var sql = "INSERT INTO tb_card_blocks (blocked_at, blocked_reason, card_id) VALUES (?,?,?);";
        try(var statement = connection.prepareStatement(sql)){
            statement.setTimestamp(1, toTimestamp(OffsetDateTime.now()));
            statement.setString(2, reason);
            statement.setLong(3, cardId);
            statement.executeUpdate();
        }
    }
    public void unblock(String reason,Long cardId)throws SQLException{
        var sql = "UPDATE tb_card_blocks SET unblocked_at = ?, unblocked_reason = ? WHERE card_id = ? AND unblocked_reason IS NULL;";
        try(var statement = connection.prepareStatement(sql)){
            statement.setTimestamp(1, toTimestamp(OffsetDateTime.now()));
            statement.setString(2, reason);
            statement.setLong(3, cardId);
            statement.executeUpdate();
        }
    }
}
