package com.sprint.mission.discodeit.async.handler;

import com.sprint.mission.discodeit.async.event.FileUploadEvent;
import com.sprint.mission.discodeit.async.failure.AsyncTaskFailure;
import com.sprint.mission.discodeit.async.failure.AsyncTaskFailureRepository;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContent.BinaryContentUploadStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadEventHandler {

  private static final String REQUEST_ID = "requestId";
  private static final String TASK_NAME = "FileUploadEvent";

  private final BinaryContentStorage binaryContentStorage;
  private final AsyncTaskFailureRepository asyncTaskFailureRepository;
  private final BinaryContentRepository binaryContentRepository;

  private final FileUploadEventHandler self = this;
  private final EntityManager entityManager;
  private final TransactionTemplate transactionTemplate;

  @Async("unifiedPool")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(FileUploadEvent event) {
    try {
      self.uploadWithRetry(event.fileId(), event.file());
      // 상태 업데이트
      binaryContentRepository.findById(event.fileId()).ifPresent(content -> {
        content.updateUploadStatus(BinaryContentUploadStatus.SUCCESS);
        binaryContentRepository.saveAndFlush(content);
      });
    } catch (Exception e) {
      self.recover(e, event.fileId(), event.file());
    }
  }

  @Retryable(
      value = {Exception.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000, multiplier = 2)
  )
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void uploadWithRetry(UUID fileId, MultipartFile file) {
    log.info("파일 저장 시도 - fileId: {}", fileId);
    log.info("파일 이름: {} / fail 요소: {}", file.getOriginalFilename(),
        file.getOriginalFilename().contains("fail"));
    binaryContentStorage.put(fileId, file).join();
  }

  @Recover
  @Transactional(propagation = Propagation.REQUIRES_NEW)  // 신규 트랜잭션 강제
  public void recover(Exception e, UUID fileId, MultipartFile file) {
    log.error("파일 저장 재시도 실패 - fileId: {}, filename: {}", fileId, file.getOriginalFilename());
    entityManager.clear();

    BinaryContent content = binaryContentRepository.findById(fileId)
        .orElseThrow(() -> new BinaryContentNotFoundException(fileId));

    content.updateUploadStatus(BinaryContentUploadStatus.FAILED);
    binaryContentRepository.saveAndFlush(content);
    binaryContentStorage.delete(fileId);

    transactionTemplate.executeWithoutResult(transactionStatus -> {
          AsyncTaskFailure failure = AsyncTaskFailure.builder()
              .requestId(MDC.get(REQUEST_ID))
              .taskName(TASK_NAME)
              .failureReason(e.getMessage())
              .build();
          asyncTaskFailureRepository.save(failure);
        }
    );

  }

}
