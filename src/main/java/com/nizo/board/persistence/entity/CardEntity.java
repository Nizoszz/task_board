package com.nizo.board.persistence.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardEntity{
    private Long id;
    private String title;
    private String description;
    private BoardColumnEntity boardColumn = new BoardColumnEntity();
}
