package com.sprint.mission.discodeit.async.handler;

import com.sprint.mission.discodeit.async.event.AsyncFailedNotificationEvent;
import com.sprint.mission.discodeit.async.event.NewMessageNotificationEvent;
import com.sprint.mission.discodeit.async.event.RoleChangedNotificationEvent;
import com.sprint.mission.discodeit.common.NotificationType;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

  private final NotificationService notificationService;

  @Async("notificationPool")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
  public void handleRoleChangedEvent(RoleChangedNotificationEvent event) {
    log.info("역할 변경 알림 생성: 수신자={}, 대상={}", event.receiverId(), event.userId());
    notificationService.create(event.receiverId(), event.userId(), NotificationType.ROLE_CHANGED);
  }

  @Async("notificationPool")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
  public void handleNewMessageEvent(NewMessageNotificationEvent event) {
    log.info("채팅 메시지 알림 생성: 채널={}, 수신자={}", event.channelId(), event.receiverId());
    notificationService.create(event.receiverId(), event.channelId(), NotificationType.NEW_MESSAGE);
  }

  @Async("notificationPool")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
  public void handleAsyncFailedEvent(AsyncFailedNotificationEvent event) {
    log.error("비동기 작업 실패 알림: 요청={}, 수신자={}", event.requestId(), event.receiverId());
    notificationService.create(event.receiverId(), event.requestId(),
        NotificationType.ASYNC_FAILED);

  }
}
