package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private final Path directory;
    private final AtomicLong idGenerator = new AtomicLong(1);

    public FileChannelRepository(@Qualifier("fileChannelStoragePath") Path directory) {
        this.directory = directory;
    }

    @PostConstruct  // 의존성 주입이 완료된 후 실행
    private void init() {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
                System.out.println("[Channel] Channel storage directory created: " + directory.toAbsolutePath());
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FAILED_TO_CREATE_DIRECTORY);
            }
        }
    }

    // 데이터 저장
    @Override
    public Long saveChannel(Channel channel) {
        Long id = idGenerator.getAndIncrement();
        Path filePath = directory.resolve(id + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(channel);
            return id;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAILED_TO_SAVE_DATA);
        }
    }

    // 유저 데이터 로드
    @Override
    public Channel loadChannel(Long id) {
        Path filePath = directory.resolve(id + ".ser");
        if (!Files.exists(filePath)) {
            throw new CustomException(ErrorCode.CHANNEL_NOT_FOUND);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new CustomException(ErrorCode.FAILED_TO_LOAD_DATA);
        }
    }

    @Override
    public Map<Long, Channel> loadAllChannels() {
        try {
            return Files.list(directory)
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            Channel channel = (Channel) ois.readObject();
                            Long id = Long.valueOf(path.getFileName().toString().replace(".ser", ""));
                            return Map.entry(id, channel);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new CustomException(ErrorCode.FAILED_TO_LOAD_DATA);
                        }
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteChannel(Long id) {
        try {
            Files.deleteIfExists(directory.resolve(id + ".ser"));
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAILED_TO_DELETE_DATA);
        }
    }

    @Override
    public void updateChannel(Long id, Channel channel) {
        Path filePath = directory.resolve(id + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAILED_TO_UPDATE_DATA);
        }
    }
}
