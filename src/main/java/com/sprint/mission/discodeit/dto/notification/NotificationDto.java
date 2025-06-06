package com.sprint.mission.discodeit.dto.notification;

import com.sprint.mission.discodeit.common.NotificationType;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record NotificationDto(UUID id, Instant createdAt, UUID receiverId, String title,
                              String content, NotificationType type, UUID targetId) {

}
