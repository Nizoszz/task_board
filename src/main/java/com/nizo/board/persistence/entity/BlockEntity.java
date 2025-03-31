package com.nizo.board.persistence.entity;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class BlockEntity{
    private Long id;
    private OffsetDateTime blockedAt;
    private String blockedReason;
    private OffsetDateTime unblockedAt;
    private String unblockedReason;
}
