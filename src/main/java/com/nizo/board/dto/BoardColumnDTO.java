package com.nizo.board.dto;

public record BoardColumnDTO(Long id,String name,com.nizo.board.persistence.entity.BoardColumnKind kind,int cardsAmount){
}
