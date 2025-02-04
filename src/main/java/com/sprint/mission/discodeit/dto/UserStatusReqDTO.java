package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;

public record UserStatusReqDTO(User user, Instant accessedAt) {
}
