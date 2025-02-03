package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Map;

public interface ChannelRepository {

    Long saveChannel(Channel channel);

    Channel loadChannel(Long id);

    Map<Long, Channel> loadAllChannels();

    void deleteChannel(Long id);

    void updateChannel(Long id, Channel channel);
}
