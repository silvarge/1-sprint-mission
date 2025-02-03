package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelReqDTO;
import com.sprint.mission.discodeit.dto.ChannelResDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ChannelService {

    // 채널 생성
    Long createChannel(User owner, String serverName, String description, String iconImgPath);

    // 멤버가 존재하는 것이 맞는지 여부 확인

    // 채널 조회
    // 특정 채널(단건)
    ChannelResDTO getChannel(Long id);

    ChannelResDTO getChannel(String uuid);

    // 모든 채널
    List<ChannelResDTO> getAllChannel();

    Channel findChannelById(Long id);

    Map.Entry<Long, Channel> findChannelByUUID(UUID uuid);

    // 채널 수정
    boolean updateChannelInfo(Long id, ChannelReqDTO updateInfo);

    // 채널 삭제
    // 채널 Status 변경 -> inactive로
    // 채널 Hard Delete -> 진짜진짜삭제
    ChannelResDTO deleteChannel(Long id);

    ChannelResDTO deleteChannel(String uuid);
}