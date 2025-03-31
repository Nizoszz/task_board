package com.nizo.board.persistence.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.nizo.board.persistence.entity.BoardColumnKind.CANCEL;
import static com.nizo.board.persistence.entity.BoardColumnKind.INIT;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEntity{
    private Long id;
    private String name;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();

//    public BoardColumnEntity getInitColumn(){
//        return getFiltedColumn(bc -> bc.getKind().equals(INIT));
//    }
//    public BoardColumnEntity getCancelColumn(){
//        return getFiltedColumn(bc -> bc.getKind().equals(CANCEL));
//    }
//    public BoardColumnEntity getFiltedColumn(Predicate<BoardColumnEntity> filter){
//        return boardColumns.stream()
//               .filter(filter)
//               .findFirst()
//               .orElseThrow();
//    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public List<BoardColumnEntity> getBoardColumns(){
        return boardColumns;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setBoardColumns(List<BoardColumnEntity> boardColumns){
        this.boardColumns = boardColumns;
    }
}
