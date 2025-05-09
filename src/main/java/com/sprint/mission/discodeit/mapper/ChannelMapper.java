package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelMapper {
    private final UserMapper userMapper;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChannelResponseDto toResponseDto(Channel channel, List<UserResponseDto> participants, Instant lastMessageAt) {

        return ChannelResponseDto.builder()
                .id(channel.getId())
                .type(channel.getChannelType())
                .name(channel.getServerName())
                .description(channel.getDescription())
                .participants(participants)
                .lastMessageAt(lastMessageAt)
                .build();
    }

    public Channel toPublicEntity(PublicChannelRequestDto publicDto) {
        User owner = userRepository.findById(publicDto.ownerId()).orElseThrow(() -> new UserNotFoundException(publicDto.ownerId()));
        return new Channel(publicDto.serverName(), Channel.ChannelType.PUBLIC, publicDto.description(), owner);
    }

    public Channel toPrivateEntity(PrivateChannelRequestDto privateDto) {
        User owner = userRepository.findById(privateDto.ownerId()).orElseThrow(() -> new UserNotFoundException(privateDto.ownerId()));
        return new Channel("", Channel.ChannelType.PRIVATE, "", owner);
    }

}
