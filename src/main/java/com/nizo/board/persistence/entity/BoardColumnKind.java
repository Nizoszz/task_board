package com.nizo.board.persistence.entity;

import java.util.stream.Stream;

public enum BoardColumnKind{
    INIT,
    PENDING,
    CANCEL,
    FINAL;
    public static BoardColumnKind findByName(String name){
        return Stream.of(BoardColumnKind.values()).filter(b -> b.name().equals(name)).findFirst().orElseThrow();
    }
}
