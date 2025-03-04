package com.sprint.mission.discodeit.dto.common;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CommonResponseDto(UUID publicId) {
    public static CommonResponseDto from(UUID publicId) {
        return CommonResponseDto.builder().publicId(publicId).build();
    }
}
