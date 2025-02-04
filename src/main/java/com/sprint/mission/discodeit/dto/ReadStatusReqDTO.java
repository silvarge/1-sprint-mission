package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ReadStatusReqDTO(User user, Channel channel, Instant lastReadAt) {
}
