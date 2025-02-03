package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record MessageResDTO(Long id, UUID uuid, User author, Channel channel, String content) {
}
