package com.nizo.board.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import com.nizo.board.dto.BoardColumnDTO;
import com.nizo.board.persistence.entity.BoardColumnEntity;
import com.nizo.board.persistence.entity.BoardColumnKind;
import com.nizo.board.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nizo.board.persistence.entity.BoardColumnKind.findByName;
import static java.util.Objects.isNull;

@AllArgsConstructor
public class BoardColumnDAO{
    private final Connection connection;

    public void save(BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO tb_board_columns (name, kind, board_column_order, board_id) VALUES(?,?,?,?);";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getKind().name());
            statement.setInt(3, entity.getBoardColumnOrder());
            statement.setLong(4, entity.getBoard().getId());
            statement.executeUpdate();
            if(statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID());
            }
        }
    }
    public List<BoardColumnEntity> findByBoardId(Long id) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, name, board_column_order FROM tb_board_columns WHERE id = ? ORDER BY board_column_order;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                entities.add(BoardColumnEntity.builder()
                       .id(resultSet.getLong("id"))
                       .name(resultSet.getString("name"))
                       .boardColumnOrder(resultSet.getInt("board_column_order"))
                                     .kind(findByName(resultSet.getString("kind")))
                       .build());
            }
            return entities;
        }
    }
    public Optional<BoardColumnEntity> findById(Long id) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql =  """
                SELECT bc.name,
                       bc.kind,
                       c.id,
                       c.title,
                       c.description
                  FROM BOARDS_COLUMNS bc
                  LEFT JOIN CARDS c
                    ON c.board_column_id = bc.id
                WHERE bc.id = ?;
            """;;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if(resultSet.next()) {
                var entity = BoardColumnEntity.builder().name(resultSet.getString("bc.name")).kind(findByName(resultSet.getString("bc.kind")));
                while (resultSet.next()) {
                    var card = CardEntity.builder();
                    if(isNull(resultSet.getString("c.title"))) {
                        card.id(resultSet.getLong("c.id")).title(resultSet.getString("c.title"))
                                .description(resultSet.getString("c.description"));
                        entity.cards(List.of(card.build()));
                    }
                }
                return Optional.of(entity.build());
            }
            return Optional.empty();
        }
    }
    public List<BoardColumnDTO> findByIdWithDetails(Long id) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql =
                """
                    SELECT bc.id,
                           bc.name,
                           bc.kind,
                           (SELECT COUNT(c.id)
                                   FROM CARDS c
                                  WHERE c.board_column_id = bc.id) cards_amount
                      FROM BOARDS_COLUMNS bc
                     WHERE board_id = ?
                     ORDER BY `order`;
                """;
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var dto = new BoardColumnDTO(
                    resultSet.getLong("bc.id"),
                    resultSet.getString("bc.name"),
                    findByName(resultSet.getString("bc.kind")),
                    resultSet.getInt("cards_amount")
                );
                dtos.add(dto);
            }
            return dtos;
        }
    }
}
