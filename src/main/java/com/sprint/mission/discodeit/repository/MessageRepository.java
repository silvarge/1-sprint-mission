package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.Map;

public interface MessageRepository {

    Long saveMessage(Message message);

    Message loadMessage(Long id);

    Map<Long, Message> loadAllMessages();

    void deleteMessage(Long id);

    void updateMessage(Long id, Message message);
}
