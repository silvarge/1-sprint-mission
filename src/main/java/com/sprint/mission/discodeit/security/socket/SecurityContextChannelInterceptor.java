package com.sprint.mission.discodeit.security.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityContextChannelInterceptor implements ChannelInterceptor {

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    log.info("🚀 SecurityContextChannelInterceptor 접근");
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    log.info("🚀 Accessor 확인: {}", accessor.getUser());

    if (accessor.getSessionId() != null) {
      if (accessor.getUser() != null) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication((Authentication) accessor.getUser());
        SecurityContextHolder.setContext(context);
      }
    }
    return message;
  }
}
