package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * - 이미지, 파일 등 바이너리 데이터를 표현
 * - 사용자의 프로필 이미지/ 메시지 첨부 파일 저장을 위해 사용
 */
@Getter
@Builder
public class BinaryContent {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    
    private byte[] data;
    private UUID userId;
    private UUID messageId;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
