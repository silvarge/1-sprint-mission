package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

@Builder
public record MessageReqDTO(User author, Channel channel, String content) {
}
