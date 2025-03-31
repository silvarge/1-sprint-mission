package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record MessageRequestDto(@NotBlank String content, @NotBlank UUID authorId, @NotBlank UUID channelId) {
}
