package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.nio.file.Path;
import java.util.Map;

public interface ChannelRepository {
    public void init(Path directory);

    public void saveChannel(Long id, Channel channel);

    public Channel loadChannel(Long id);

    public Map<Long, Channel> loadAllChannels();

    public void deleteChannel(Long id);

    public long getNextId();
}
