package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadEventHandler {

  private final BinaryContentStorage binaryContentStorage;

  @Async("unifiedPool")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(FileUploadEvent event) {
    UUID fileId = event.fileId();
    MultipartFile file = event.file();
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    log.info("비동기 핸들러 내 인증 객체: {}", auth);
    log.info("비동기 핸들러 내 사용자명: {}", auth != null ? auth.getName() : "null");

    try {
      binaryContentStorage.put(fileId, file).join();
    } catch (Exception e) {
      log.error("비동기 파일 저장 실패 - id: {}, filename: {}", fileId, file.getOriginalFilename(), e);
    }
  }
}
