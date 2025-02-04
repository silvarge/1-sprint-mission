package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.common.validation.ChannelValidator;
import com.sprint.mission.discodeit.common.validation.Validator;
import com.sprint.mission.discodeit.dto.ChannelReqDTO;
import com.sprint.mission.discodeit.dto.ChannelResDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import io.micrometer.common.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {

    private final Validator<Channel, ChannelReqDTO> channelValidator = new ChannelValidator();
    private final ChannelRepository channelRepository;

    public JCFChannelService() {
        this.channelRepository = new JCFChannelRepository();
    }

    @Override
    public Long createChannel(User owner, String serverName, String description, String iconImgPath) {
        // DTO에 user 객체 + 필요 정보 넣어서 전달되면
        // 생성
        try {
            ChannelReqDTO channelDto = ChannelReqDTO.builder()
                    .owner(owner)
                    .serverName(serverName)
                    .description(StringUtils.isEmpty(description) ? "" : description)
                    .iconImgPath(StringUtils.isEmpty(iconImgPath) ? "defaultSeverIcon.png" : iconImgPath)
                    .build();

            channelValidator.validateCreate(channelDto);
            return channelRepository.saveChannel(new Channel(channelDto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChannelResDTO getChannel(Long id) {
        Channel channel = findChannelById(id);
        if (channel == null) throw new CustomException(ErrorCode.MESSAGE_NOT_FOUND);

        return ChannelResDTO.builder()
                .id(id)
                .uuid(channel.getId())
                .owner(channel.getOwner())
                .serverName(channel.getServerName())
                .description(channel.getDescription())
                .iconImgPath(channel.getIconImgPath())
                .build();
    }

    @Override
    public ChannelResDTO getChannel(String uuid) {
        return channelRepository.loadAllChannels().entrySet().stream()
                .filter(entry -> entry.getValue().getId().toString().equals(uuid))
                .findFirst()
                .map(entry -> ChannelResDTO.builder()
                        .id(entry.getKey())
                        .uuid(entry.getValue().getId())
                        .owner(entry.getValue().getOwner())
                        .serverName(entry.getValue().getServerName())
                        .description(entry.getValue().getDescription())
                        .iconImgPath(entry.getValue().getIconImgPath())
                        .build()
                )
                .orElseThrow(() -> new CustomException(ErrorCode.CHANNEL_NOT_FOUND));
    }


    @Override
    public List<ChannelResDTO> getAllChannel() {
        return channelRepository.loadAllChannels().entrySet().stream()
                .map(entry -> ChannelResDTO.builder()
                        .id(entry.getKey())
                        .uuid(entry.getValue().getId())
                        .owner(entry.getValue().getOwner())
                        .serverName(entry.getValue().getServerName())
                        .description(entry.getValue().getDescription())
                        .iconImgPath(entry.getValue().getIconImgPath())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public Channel findChannelById(Long id) {
        return channelRepository.loadChannel(id);
    }

    @Override
    public Map.Entry<Long, Channel> findChannelByUUID(UUID uuid) {
        Map<Long, Channel> allChannels = channelRepository.loadAllChannels();
        return allChannels.entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.CHANNEL_NOT_FOUND));
    }

    @Override
    public boolean updateChannelInfo(Long id, ChannelReqDTO updateInfo) {
        boolean isUpdated = false;
        try {
            Channel channel = findChannelById(id);

            channelRepository.updateChannel(id, channel);
            return isUpdated;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 채널 삭제
    @Override
    public ChannelResDTO deleteChannel(Long id) {
        ChannelResDTO deleteChannel = getChannel(id);
        channelRepository.deleteChannel(id);
        return deleteChannel;
    }

    @Override
    public ChannelResDTO deleteChannel(String uuid) {
        ChannelResDTO deleteChannel = getChannel(uuid);
        channelRepository.deleteChannel(deleteChannel.id());
        return deleteChannel;
    }

}
