package com.sprint.mission.discodeit.async.event;

import com.sprint.mission.discodeit.common.NotificationType;
import java.util.UUID;

public record AsyncFailedNotificationEvent(UUID receiverId, UUID requestId, NotificationType type) {

}
