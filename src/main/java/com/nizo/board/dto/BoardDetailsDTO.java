package com.nizo.board.dto;

import com.nizo.board.persistence.entity.BoardColumnEntity;

import java.util.List;

public record BoardDetailsDTO(Long id,String name,List<BoardColumnDTO> columns){
}
