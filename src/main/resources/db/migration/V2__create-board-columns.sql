CREATE TABLE IF NOT EXISTS tb_board_columns(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    kind VARCHAR(50) NOT NULL,
    board_column_order INT NOT NULL,
    board_id BIGINT NOT NULL,
    CONSTRAINT boards__boards_columns_fk FOREIGN KEY (board_id) REFERENCES tb_boards(id) ON DELETE CASCADE,
    CONSTRAINT id__order_uk UNIQUE unique_board_id_order (board_id, board_column_order)
);