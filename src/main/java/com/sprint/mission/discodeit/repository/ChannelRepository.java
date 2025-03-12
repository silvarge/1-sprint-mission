package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Collection<Object> findAllByOwner(User owner);

    Collection<Object> getChannelsByOwner(User owner);

    Channel findById(UUID id);
}
