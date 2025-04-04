package com.nizo.board.dto;

import com.nizo.board.persistence.entity.BoardColumnKind;

public record BoardColumnInfoDto(Long id,int boardColumnOrder,BoardColumnKind kind){
}
