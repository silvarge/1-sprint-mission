package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.nio.file.Path;
import java.util.Map;

public interface UserRepository {

    public void init(Path directory);

    public void saveUser(Long id, User user);

    public User loadUser(Long id);

    public Map<Long, User> loadAllUsers();

    public void deleteUser(Long id);
    
    public long getNextId();
}
