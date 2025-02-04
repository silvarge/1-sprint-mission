package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Builder
public record BinaryContentReqDTO(MultipartFile file, UUID userId, UUID messageId) {
}

// TODO: @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) 찾아보고 적용 고려해보기
