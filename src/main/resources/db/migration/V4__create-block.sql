CREATE TABLE IF NOT EXISTS tb_card_blocks(
    id BIGINT PRIMARY KEY,
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    blocked_reason TEXT NOT NULL,
    unblocked_at TIMESTAMP DEFAULT NULL,
    unblocked_reason TEXT NOT NULL,
    card_id BIGINT NOT NULL,
    CONSTRAINT cards__blocks_fk FOREIGN KEY (card_id) REFERENCES tb_cards(id) ON DELETE CASCADE
)