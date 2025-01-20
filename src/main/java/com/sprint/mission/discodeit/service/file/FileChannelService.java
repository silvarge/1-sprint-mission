package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.validation.Validator;
import com.sprint.mission.discodeit.common.validation.ValidatorImpl;
import com.sprint.mission.discodeit.dto.ChannelReqDTO;
import com.sprint.mission.discodeit.dto.ChannelResDTO;
import com.sprint.mission.discodeit.dto.ChannelUpdateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.service.ChannelService;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {

    private final Validator validator = new ValidatorImpl();
    private final Path directory;
    private final AtomicLong idGenerator;

    public FileChannelService(Path directory) {
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
    private void saveChannel(Long id, Channel channel) {
        Path filePath = directory.resolve(id + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save channel data: " + id, e);
        }
    }

    // 유저 데이터 로드
    private Channel loadChannel(Long id) {
        Path filePath = directory.resolve(id + ".ser");
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Channel data not found for ID: " + id);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load channel data: " + id, e);
        }
    }

    private Map<Long, Channel> loadAllChannels() {
        try {
            return Files.list(directory)
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            Channel channel = (Channel) ois.readObject();
                            Long id = Long.valueOf(path.getFileName().toString().replace(".ser", ""));
                            return Map.entry(id, channel);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("Failed to load user data from file: " + path, e);
                        }
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load channels from directory", e);
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
    public Long createChannel(User owner, String serverName, String description, String iconImgPath) {
        // DTO에 user 객체 + 필요 정보 넣어서 전달되면
        // 생성
        try {
            if (owner == null) {
                throw new CustomException(ErrorCode.OWNER_CANNOT_BLANK);
            }

            if (serverName == null) {
                throw new CustomException(ErrorCode.SERVERNAME_CANNOT_BLANK);
            }

            description = StringUtils.isEmpty(description) ? "" : description;
            iconImgPath = StringUtils.isEmpty(iconImgPath) ? "defaultSeverIcon.png" : iconImgPath;

            Channel channel = new Channel(new ChannelReqDTO(
                    owner, serverName, description, iconImgPath
            ));
            Long id = idGenerator.getAndIncrement();
            saveChannel(id, channel);
            return id;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChannelResDTO getChannel(Long id) {
        Channel channel = Objects.requireNonNull(loadChannel(id), "해당 ID의 채널이 존재하지 않습니다.");
        return new ChannelResDTO(id, channel, channel.getOwner());
    }

    @Override
    public Channel getChannelToChannelObj(Long id) {
        return Objects.requireNonNull(loadChannel(id), "해당 ID의 채널이 존재하지 않습니다.");
    }

    // TODO: 'Optional. get()' without 'isPresent()' check <- 확인
    @Override
    public ChannelResDTO getChannel(String uuid) {
        Map<Long, Channel> allChannels = loadAllChannels();
        return allChannels.entrySet().stream()
                .filter(entry -> entry.getValue().getId().toString().equals(uuid))
                .findFirst()
                .map(entry -> new ChannelResDTO(entry.getKey(), entry.getValue(), entry.getValue().getOwner()))
                .orElseThrow(() -> new RuntimeException("해당 ID의 채널이 존재하지 않습니다."));
    }


    @Override
    public List<ChannelResDTO> getAllChannel() {
        return loadAllChannels().entrySet().stream()
                .map(entry ->
                        new ChannelResDTO(entry.getKey(), entry.getValue(), entry.getValue().getOwner()))
                .collect(Collectors.toList());
    }

    @Override
    public Channel findChannelById(Long id) {
        return loadChannel(id);
    }

    @Override
    public Optional<Map.Entry<Long, Channel>> findChannelByUUID(UUID uuid) {
        Map<Long, Channel> allChannels = loadAllChannels();
        return allChannels.entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(uuid))
                .findFirst();
    }

    // 채널 제목 수정
    // 채널 소개 수정
    // 채널 이미지 수정
    // 채널 주인 양도 (소유권 이전)
    @Override
    public boolean updateChannelInfo(Long id, ChannelUpdateDTO updateInfo) {
        boolean isUpdated = false;
        try {
            Channel channel = getChannelToChannelObj(id);
            if (updateInfo.getOwner() != null && !channel.getOwner().getUserName().getName().equals(updateInfo.getOwner().getUserName().getName())) {
                channel.updateOwner(updateInfo.getOwner());
                isUpdated = true;
            }

            if (updateInfo.getServerName() != null && !channel.getServerName().getName().equals(updateInfo.getServerName())) {
                channel.updateServerName(updateInfo.getServerName());
                isUpdated = true;
            }

            if (updateInfo.getDescription() != null && !channel.getDescription().equals(updateInfo.getDescription())) {
                channel.updateDescription(updateInfo.getDescription());
                isUpdated = true;
            }

            if (updateInfo.getIconImgPath() != null && !channel.getIconImgPath().equals(updateInfo.getIconImgPath())) {
                channel.updateIconImgPath(updateInfo.getIconImgPath());
                isUpdated = true;
            }
            saveChannel(id, channel);
            return isUpdated;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 채널 삭제
    @Override
    public ChannelResDTO deleteChannel(Long id) {
        ChannelResDTO deleteChannel = getChannel(id);
        Path filePath = directory.resolve(id + ".ser");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return deleteChannel;
    }

    @Override
    public ChannelResDTO deleteChannel(String uuid) {
        ChannelResDTO deleteChannel = getChannel(uuid);
        Path filePath = directory.resolve(deleteChannel.getId() + ".ser");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return deleteChannel;
    }
}
