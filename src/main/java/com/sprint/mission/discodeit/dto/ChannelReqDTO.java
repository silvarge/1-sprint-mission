package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.ChannelType;
import lombok.Builder;

@Builder
public record ChannelReqDTO(User owner, String serverName, String description, String iconImgPath,
                            ChannelType channelType) {
}
