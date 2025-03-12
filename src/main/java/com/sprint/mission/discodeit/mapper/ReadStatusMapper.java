package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadStatusMapper {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ReadStatusResponseDto toResponseDto(ReadStatus readStatus) {
        return ReadStatusResponseDto.builder()
                .id(readStatus.getId())
                .userId(readStatus.getUser().getId())
                .channelId(readStatus.getChannel().getId())
                .lastReadAt(readStatus.getLastReadAt())
                .build();
    }

    public ReadStatus toEntity(ReadStatusRequestDto readStatusRequestDto) {
        return new ReadStatus(
                userRepository.findById(readStatusRequestDto.userId()),
                channelRepository.findById(readStatusRequestDto.channelId()),
                readStatusRequestDto.lastReadAt()
        );
    }
}
