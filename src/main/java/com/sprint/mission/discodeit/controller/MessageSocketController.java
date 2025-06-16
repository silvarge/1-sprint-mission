package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageSocketController {

  private final MessageService messageService;
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/messages")
  public void sendMessage(MessageRequestDto chatMessage) throws IOException {
    // todo: 메시지 서비스 생성 시 첨부파일 안 담긴거라.. 새 메서드를 짓는 것이 좋지 않을까?
    MessageResponseDto response = messageService.create(chatMessage, null);

    String destination = "/sub/channels." + chatMessage.channelId() + ".messages";
    log.info("🐰 broadcast destination: {} / response: {}", destination, response);
    messagingTemplate.convertAndSend(destination, response);
  }

}
