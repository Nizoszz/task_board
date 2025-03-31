CREATE TABLE IF NOT EXISTS tb_cards(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    card_order int NOT NULL,
    board_column_id BIGINT NOT NULL,
    CONSTRAINT board_columns__cards_fk FOREIGN KEY (board_column_id) REFERENCES tb_board_columns(id) ON DELETE CASCADE
)