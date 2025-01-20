package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.validation.Validator;
import com.sprint.mission.discodeit.common.validation.ValidatorImpl;
import com.sprint.mission.discodeit.dto.MessageReqDTO;
import com.sprint.mission.discodeit.dto.MessageResDTO;
import com.sprint.mission.discodeit.dto.MessageUpdateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {

    private final Validator validator = new ValidatorImpl();
    private final Path directory;
    private final AtomicLong idGenerator;

    public FileMessageService(Path directory) {
        this.directory = directory;
        init(directory);
        this.idGenerator = new AtomicLong(1);
    }

    // 파일 입출력 관련 ========================
    // 디렉토리 초기화
    private void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + directory, e);
            }
        }
    }

    // 데이터 저장
    private void saveMessage(Long id, Message message) {
        Path filePath = directory.resolve(id + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save message data: " + id, e);
        }
    }

    // 유저 데이터 로드
    private Message loadMessage(Long id) {
        Path filePath = directory.resolve(id + ".ser");
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Message data not found for ID: " + id);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load message data: " + id, e);
        }
    }

    private Map<Long, Message> loadAllMessages() {
        try {
            return Files.list(directory)
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            Message message = (Message) ois.readObject();
                            Long id = Long.valueOf(path.getFileName().toString().replace(".ser", ""));
                            return Map.entry(id, message);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("Failed to load message data from file: " + path, e);
                        }
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load messages from directory", e);
        }
    }

    // ID 초기값 계산 (디렉터리 내 파일 개수 기반)
    private long getNextId() {
        try {
            return Files.list(directory).count() + 1;
        } catch (IOException e) {
            throw new RuntimeException("Failed to calculate next ID", e);
        }
    }
    // 파일 입출력 관련 ========================

    @Override
    public Long createMessage(User author, Channel channel, String content) {
        try {
            if (author == null) {
                throw new CustomException(ErrorCode.AUTHOR_CANNOT_BLANK);
            }
            if (channel == null) {
                throw new CustomException(ErrorCode.CHANNEL_CANNOT_BLANK);
            }
            if (content == null) {
                throw new CustomException(ErrorCode.CONTENT_CANANOT_BLANK);
            }

            Message msg = new Message(new MessageReqDTO(author, channel, content));
            Long id = idGenerator.getAndIncrement();
            saveMessage(id, msg);
            return id;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MessageResDTO getMessage(Long id) {
        Message msg = Objects.requireNonNull(loadMessage(id), "해당 ID의 메시지가 존재하지 않습니다.");
        return new MessageResDTO(id, msg);
    }

    @Override
    public MessageResDTO getMessage(String uuid) {
        Map<Long, Message> allMessages = loadAllMessages();
        return allMessages.entrySet().stream()
                .filter(entry -> entry.getValue().getId().toString().equals(uuid))
                .findFirst()
                .map(entry -> new MessageResDTO(entry.getKey(), entry.getValue()))
                .orElseThrow(() -> new RuntimeException("해당 ID의 메시지가 존재하지 않습니다."));
    }

    @Override
    public Message getMessageToMsgObj(Long id) {
        return Objects.requireNonNull(loadMessage(id), "해당 ID의 메시지가 존재하지 않습니다.");
    }

    @Override
    public List<MessageResDTO> getAllMessage() {
        return loadAllMessages().entrySet().stream()
                .map(entry ->
                        new MessageResDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResDTO> getChannelMessage(String channelName) {
        return loadAllMessages().entrySet().stream()
                .filter(entry -> entry.getValue().getChannel().getId().equals(UUID.fromString(channelName)))
                .map(entry ->
                        new MessageResDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public Message findMessageById(Long id) {
        return loadAllMessages().get(id);
    }

    public Optional<Map.Entry<Long, Message>> findMessageByUUID(String uuid) {
        return loadAllMessages().entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(UUID.fromString(uuid)))
                .findFirst();
    }

    @Override
    public boolean updateMessage(Long id, MessageUpdateDTO updateInfo) {
        boolean isUpdated = false;
        try {
            Message msg = getMessageToMsgObj(id);
            if (updateInfo.getContent() != null && !msg.getContent().equals(updateInfo.getContent())) {
                msg.updateContent(updateInfo.getContent());
                isUpdated = true;
            }
            saveMessage(id, msg);
            return isUpdated;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MessageResDTO deleteMessage(Long id) {
        MessageResDTO deleteMessage = getMessage(id);
        Path filePath = directory.resolve(id + ".ser");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return deleteMessage;
    }

    @Override
    public MessageResDTO deleteMessage(String uuid) {
        MessageResDTO deleteMessage = getMessage(uuid);
        Path filePath = directory.resolve(deleteMessage.getId() + ".ser");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return deleteMessage;
    }
}
