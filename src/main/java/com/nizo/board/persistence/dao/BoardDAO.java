package com.nizo.board.persistence.dao;
import com.mysql.cj.jdbc.StatementImpl;
import com.nizo.board.persistence.entity.BoardEntity;
import lombok.RequiredArgsConstructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@RequiredArgsConstructor
public class BoardDAO{
    private final Connection connection;

    public void save(BoardEntity entity) throws SQLException {
        var sql = "INSERT INTO tb_boards (name) VALUES(?);";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.executeUpdate();
            if(statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }
        }
    }
    public void deleteById(Long id) throws SQLException {
        var sql = "DELETE FROM tb_boards WHERE id = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }
    public Optional<BoardEntity> findById (Long id) throws SQLException {
        var sql = "SELECT id, name FROM tb_boards WHERE id =?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if(resultSet.next()){
                return Optional.of(BoardEntity.builder()
                       .id(resultSet.getLong("id"))
                       .name(resultSet.getString("name"))
                       .build());
            }
        }
        return Optional.empty();
    }
    public boolean boardExists (Long id) throws SQLException {
        var sql = "SELECT COUNT(*) FROM tb_boards WHERE id =?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                if(resultSet.next()){
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
