package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.validation.MessageValidator;
import com.sprint.mission.discodeit.common.validation.Validator;
import com.sprint.mission.discodeit.dto.MessageReqDTO;
import com.sprint.mission.discodeit.dto.MessageResDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {
    private final Validator<Message, MessageReqDTO> messageValidator = new MessageValidator();
    private final MessageRepository messageRepository;

    public FileMessageService(Path directory) {
        this.messageRepository = new FileMessageRepository(directory);
    }

    @Override
    public Long createMessage(User author, Channel channel, String content) {
        try {
            MessageReqDTO msgDto = MessageReqDTO.builder()
                    .author(author)
                    .channel(channel)
                    .content(content)
                    .build();
            messageValidator.validateCreate(msgDto);
            return messageRepository.saveMessage(new Message(msgDto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MessageResDTO getMessage(Long id) {
        Message msg = findMessageById(id);
        if (msg == null) throw new CustomException(ErrorCode.MESSAGE_NOT_FOUND);
        return MessageResDTO.builder()
                .id(id)
                .uuid(msg.getId())
                .author(msg.getAuthor())
                .channel(msg.getChannel())
                .content(msg.getContent())
                .build();
    }

    @Override
    public MessageResDTO getMessage(String uuid) {
        Map.Entry<Long, Message> msg = findMessageByUUID(uuid);
        if (msg == null) throw new CustomException(ErrorCode.MESSAGE_NOT_FOUND);
        return MessageResDTO.builder()
                .id(msg.getKey())
                .uuid(msg.getValue().getId())
                .author(msg.getValue().getAuthor())
                .channel(msg.getValue().getChannel())
                .content(msg.getValue().getContent())
                .build();
    }

    @Override
    public List<MessageResDTO> getAllMessage() {
        return messageRepository.loadAllMessages().entrySet().stream()
                .map(entry -> MessageResDTO.builder()
                        .id(entry.getKey())
                        .uuid(entry.getValue().getId())
                        .author(entry.getValue().getAuthor())
                        .channel(entry.getValue().getChannel())
                        .content(entry.getValue().getContent())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResDTO> getChannelMessage(String channelName) {
        return messageRepository.loadAllMessages().entrySet().stream()
                .filter(entry -> entry.getValue().getChannel().getId().equals(UUID.fromString(channelName)))
                .map(entry -> MessageResDTO.builder()
                        .id(entry.getKey())
                        .uuid(entry.getValue().getId())
                        .author(entry.getValue().getAuthor())
                        .channel(entry.getValue().getChannel())
                        .content(entry.getValue().getContent())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public Message findMessageById(Long id) {
        return messageRepository.loadMessage(id);
    }

    @Override
    public Map.Entry<Long, Message> findMessageByUUID(String uuid) {
        return messageRepository.loadAllMessages().entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(UUID.fromString(uuid)))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.CHANNEL_NOT_FOUND));
    }

    @Override
    public boolean updateMessage(Long id, MessageReqDTO updateInfo) {
        boolean isUpdated = false;
        try {
            Message msg = findMessageById(id);
            messageRepository.updateMessage(id, msg);
            return isUpdated;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MessageResDTO deleteMessage(Long id) {
        MessageResDTO deleteMessage = getMessage(id);
        messageRepository.deleteMessage(id);
        return deleteMessage;
    }

    @Override
    public MessageResDTO deleteMessage(String uuid) {
        MessageResDTO deleteMessage = getMessage(uuid);
        messageRepository.deleteMessage(deleteMessage.id());
        return deleteMessage;
    }
}
