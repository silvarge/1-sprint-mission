package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelMapper channelMapper;

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ChannelResponseDto createPublicChannel(PublicChannelRequestDto channelReqDTO) {
        Channel savedChannel = channelRepository.saveAndFlush(channelMapper.toPublicEntity(channelReqDTO));
        return channelMapper.toResponseDto(savedChannel);
    }

    @Transactional
    @Override
    public ChannelResponseDto createPrivateChannel(PrivateChannelRequestDto channelReqDTO) {
        Channel channel = channelMapper.toPrivateEntity(channelReqDTO);
        List<User> users = channelReqDTO.participantIds().stream()
                .map(userRepository::findById)
                .filter(user -> !channel.getMembers().contains(user))
                .peek(user -> user.getJoinChannels().add(channel))
                .toList();

        channel.getMembers().addAll(users);
        Channel savedChannel = channelRepository.saveAndFlush(channel);

        return channelMapper.toResponseDto(channelRepository.findById(savedChannel.getId()));
    }

    @Override
    public ChannelResponseDto find(UUID uuid) {
        return channelMapper.toResponseDto(channelRepository.findById(uuid));
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        User owner = userRepository.findById(userId);
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getOwner().equals(owner))
                .map(channelMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ChannelResponseDto update(UUID channelId, ChannelUpdateDto updateDTO) {
        Channel channel = channelRepository.findById(channelId);
        if (channel.getChannelType() == Channel.ChannelType.PRIVATE) {
            throw new CustomException(ErrorCode.PRIVATE_CANNOT_MODIFY);
        }

        channel.updateServerName(updateDTO.name());
        channel.updateDescription(updateDTO.description());

        return channelMapper.toResponseDto(channel);
    }

    @Transactional
    @Override
    public ChannelResponseDto delete(UUID channelId) {
        Channel deleteChannel = channelRepository.findById(channelId);
        channelRepository.delete(deleteChannel);
        return channelMapper.toResponseDto(deleteChannel);
    }
}
