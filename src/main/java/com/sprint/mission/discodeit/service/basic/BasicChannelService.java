package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelMember;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelCanNotModifyException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelMemberRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelMapper channelMapper;

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ChannelResponseDto createPublicChannel(PublicChannelRequestDto channelReqDTO) {
        log.debug("public 채널 생성 요청 - 생성 요청 데이터: {}", channelReqDTO);
        Channel savedChannel = channelRepository.saveAndFlush(channelMapper.toPublicEntity(channelReqDTO));
        log.info("public 채널이 생성되었습니다. - id: {}", savedChannel.getId());
        return channelMapper.toResponseDto(savedChannel);
    }

    @Transactional
    @Override
    public ChannelResponseDto createPrivateChannel(PrivateChannelRequestDto channelReqDTO) {
        log.debug("private 채널 생성 요청 - 생성 요청 데이터: {}", channelReqDTO);

        Channel channel = channelMapper.toPrivateEntity(channelReqDTO);
        Channel savedChannel = channelRepository.save(channel);

        List<User> users = channelReqDTO.participantIds().stream()
                .map(userRepository::findById)
                .flatMap(Optional::stream)
                .toList();

        // 3. ChannelMember 엔티티 생성 및 저장
        List<ChannelMember> channelMembers = users.stream()
                .map(user -> new ChannelMember(user, savedChannel))
                .toList();

        channelMemberRepository.saveAll(channelMembers);  // 명시적으로 저장 (더 안전함)
        log.info("private 채널 멤버가 저장되었습니다. - channelId: {}, 멤버 수: {}", savedChannel.getId(), channelMembers.size());

        log.info("private 채널이 생성되었습니다. - id: {}", savedChannel.getId());
        return channelMapper.toResponseDto(savedChannel);
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        log.debug("채널 단건 조회 요청 - id: {}", channelId);
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        log.info("채널 단건 조회에 성공하였습니다. - id: {}", channelId);
        return channelMapper.toResponseDto(channel);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        log.debug("사용자 별 채널 전체 조회 요청 - userId: {}", userId);
        List<ChannelResponseDto> result = channelRepository.findAllByUserId(userId).stream()
                .map(channelMapper::toResponseDto)
                .collect(Collectors.toList());
        log.info("사용자 별 채널 전체 조회가 완료되었습니다. - userId: {}, 채널 수: {}", userId, result.size());
        return result;
    }

    @Transactional
    @Override
    public ChannelResponseDto update(UUID channelId, ChannelUpdateDto updateDTO) {
        log.debug("private 채널 수정 요청 - 수정 대상 id: {}, 생성 요청 데이터: {}", channelId, updateDTO);
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));

        if (channel.getChannelType() == Channel.ChannelType.PRIVATE) {
            log.warn("수정할 수 없는 채널 유형입니다. - id: {}, type: {}", channel.getId(), channel.getChannelType());
            throw new PrivateChannelCanNotModifyException(channel.getId(), channel.getChannelType());
        }

        channel.updateServerName(updateDTO.name());
        channel.updateDescription(updateDTO.description());

        log.info("private 채널 정보가 수정되었습니다. - id: {}", channel.getId());
        return channelMapper.toResponseDto(channel);
    }

    @Transactional
    @Override
    public ChannelResponseDto delete(UUID channelId) {
        log.debug("private 채널 삭제 요청 - 삭제 대상 id: {}", channelId);
        Channel deleteChannel = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));

        channelRepository.delete(deleteChannel);

        log.info("대상 채널이 삭제되었습니다. - id: {}", deleteChannel.getId());
        return channelMapper.toResponseDto(deleteChannel);
    }
}
