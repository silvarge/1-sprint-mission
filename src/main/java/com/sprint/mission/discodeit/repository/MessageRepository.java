package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.nio.file.Path;
import java.util.Map;

public interface MessageRepository {
    public void init(Path directory);

    public void saveMessage(Long id, Message message);

    public Message loadMessage(Long id);

    public Map<Long, Message> loadAllMessages();

    public void deleteMessage(Long id);

    public long getNextId();
}
