package com.nizo.board.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import com.nizo.board.dto.CardDetailsDTO;
import com.nizo.board.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static com.nizo.board.persistence.util.OffDateTimeConverter.toOffsetDateTime;
import static java.util.Objects.nonNull;

@AllArgsConstructor
public class CardDAO{
    private Connection connection;

    public void save(CardEntity entity) throws SQLException{
        var sql = """
                    INSERT INTO tb_cards(title, description, board_column_id)
                    VALUES(?,?,?);
                    """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());
            statement.setLong(3, entity.getBoardColumn().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }
        }
    }
    public Optional<CardDetailsDTO> findById(Long id, Long boardId) throws SQLException{
        var sql = """
                        SELECT c.id,
                               c.title,
                               c.description,
                               b.blocked_at,
                               b.blocked_reason,
                               c.board_column_id,
                               bc.name,
                               (SELECT COUNT(sub_b.id)
                                       FROM tb_card_blocks sub_b
                                      WHERE sub_b.card_id = c.id) blocks_amount
                        FROM tb_cards c
                        LEFT JOIN tb_card_blocks b
                            ON c.id = b.card_id
                            AND b.unblocked_at IS NULL
                        INNER JOIN tb_board_columns bc
                            ON bc.id = c.board_column_id
                        INNER JOIN tb_boards board
                            ON board.id = bc.board_id
                        WHERE c.id = ?
                        AND board.id = ?;
                   """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()) {
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        nonNull(resultSet.getString("b.blocked_reason")),
                        toOffsetDateTime(resultSet.getTimestamp("b.blocked_at")),
                        resultSet.getString("b.blocked_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_column_id"),
                        resultSet.getString("bc.name")
                );
                return Optional.of(dto);
            }
            return Optional.empty();
        }
    }
}
