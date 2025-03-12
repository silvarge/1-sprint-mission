package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentService binaryContentService;

    private final MessageMapper messageMapper;
    private final PageResponseMapper<MessageResponseDto> pageResponseMapper;

    private void saveAttachmentList(Message message, List<MultipartFile> attachments) throws IOException {
        for (MultipartFile attachment : attachments) {
            UUID id = binaryContentService.create(attachment).id();
            BinaryContent binaryContent = binaryContentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.FAILED_TO_LOAD_DATA));

            message.getAttachments().add(binaryContent);
            binaryContent.getMessages().add(message);
        }
    }

    @Transactional
    @Override
    public MessageResponseDto create(MessageRequestDto messageReqDTO, List<MultipartFile> attachments) throws IOException {
        Message message = messageMapper.toEntity(messageReqDTO);
        if (attachments != null && !attachments.isEmpty()) {
            saveAttachmentList(message, attachments);
        }
        messageRepository.save(message);
        return messageMapper.toResponseDto(message);
    }

    @Override
    public MessageResponseDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new CustomException(ErrorCode.FAILED_TO_LOAD_DATA));
        return messageMapper.toResponseDto(message);
    }

    @Override
    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .map(messageMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<MessageResponseDto> findMessagesByChannelId(UUID channelId, int page) {
        int MESSAGE_PAGE_SIZE = 50;
        Pageable pageable = PageRequest.of(page, MESSAGE_PAGE_SIZE);
        Slice<Message> messages = messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, pageable);
        return pageResponseMapper.fromSlice(messages.map(messageMapper::toResponseDto));
    }

    @Transactional
    @Override
    public MessageResponseDto update(UUID messageId, String content, List<MultipartFile> attachments) throws IOException {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new CustomException(ErrorCode.FAILED_TO_LOAD_DATA));
        message.updateContent(content);

        if (!attachments.isEmpty()) {
            if (!message.getAttachments().isEmpty()) {
                message.clearAttachments();
                messageRepository.save(message);
            }
            saveAttachmentList(message, attachments);
        }

        return messageMapper.toResponseDto(message);
    }

    @Transactional
    @Override
    public MessageResponseDto delete(UUID messageId) {
        Message deletedMessage = messageRepository.findById(messageId).orElseThrow(() -> new CustomException(ErrorCode.FAILED_TO_LOAD_DATA));
        MessageResponseDto response = messageMapper.toResponseDto(deletedMessage);
        // 메시지에 연결된 첨부 파일 삭제
        deletedMessage.getAttachments().clear();
        messageRepository.save(deletedMessage); // 관계 먼저 제거

        messageRepository.delete(deletedMessage);
        return response;
    }
}
