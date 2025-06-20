package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.socket.CustomAuthorizationChannelInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebSocketSecurityConfig {

  @Bean
  public AuthorizationManager<Message<?>> messageAuthorizationManager() {
    return (authentication, message) -> {
      StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
      log.info("🔐 인가 검증: command={}, dest={}, user={}",
          accessor.getCommand(),
          accessor.getDestination(),
          (authentication != null) ? authentication.get() : "null"
      );
      return MessageMatcherDelegatingAuthorizationManager.builder()
          .simpTypeMatchers(
              SimpMessageType.CONNECT,
              SimpMessageType.DISCONNECT,
              SimpMessageType.UNSUBSCRIBE
          ).permitAll()
          .simpDestMatchers("/pub/**").authenticated()
          .simpSubscribeDestMatchers("/sub/**").authenticated()
          .anyMessage().denyAll()
          .build().check(authentication, message);
    };
//    return MessageMatcherDelegatingAuthorizationManager.builder()
//        // 시스템 메시지 허용 (인증 없이)
//        .simpTypeMatchers(
//            SimpMessageType.CONNECT,
//            SimpMessageType.DISCONNECT,
//            SimpMessageType.UNSUBSCRIBE
//        ).permitAll()
//        // 애플리케이션 메시지 인증 요구
//        .simpDestMatchers("/pub/**").authenticated()
//        .simpSubscribeDestMatchers("/sub/**").authenticated()
//        // 기타 모든 메시지 차단
//        .anyMessage().denyAll()
//        .build();
  }

  @Bean
  public CustomAuthorizationChannelInterceptor authorizationChannelInterceptor(
      AuthorizationManager<Message<?>> messageAuthorizationManager
  ) {
    return new CustomAuthorizationChannelInterceptor(messageAuthorizationManager);
  }

}
