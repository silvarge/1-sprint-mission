package com.sprint.mission.discodeit.async.event;

import com.sprint.mission.discodeit.common.NotificationType;
import java.util.UUID;

public record RoleChangedNotificationEvent(UUID receiverId, UUID userId, NotificationType type) {

}
