package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.NotificationType;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public NotificationDto create(UUID receiverId, UUID targetId, NotificationType type) {
    User receiver = userRepository.findById(receiverId)
        .orElseThrow(() -> new UserNotFoundException(receiverId));

    Notification notification = Notification.builder()
        .receiverId(receiver.getId())
        .notificationType(type)
        .targetId(targetId)
        .title(type.getTitle())
        .content(type.getMessage())
        .build();

    log.info("✨ 알림 생성: {}", notification);
    Notification saved = notificationRepository.save(notification);

    return notificationMapper.toResponseDto(saved);
  }

  @Override
  public List<NotificationDto> getNotifications(UUID userId) {
    log.info("✨ 사용자 아이디: {}, 타입: {}", userId, userId.getClass().getName());

    log.info("✨ 받아 오기는 잘하니? {}", notificationRepository.findAll());

    return notificationRepository.getAllByReceiverId(userId)
        .stream()
        .map(notificationMapper::toResponseDto)
        .toList();
  }

  @Override
  public void deleteNotification(UUID notificationId, UUID userId) {
    Notification notification = notificationRepository.findById(notificationId).orElseThrow();
    notificationRepository.delete(notification);
  }

}
