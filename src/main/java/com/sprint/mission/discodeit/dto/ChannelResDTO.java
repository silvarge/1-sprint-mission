package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.common.Name;
import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ChannelResDTO(Long id, UUID uuid, User owner, Name serverName, String description, String iconImgPath) {
}
