package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.Map;

public interface UserRepository {

    Long saveUser(User user);

    User loadUser(Long id);

    Map<Long, User> loadAllUsers();

    void deleteUser(Long id);

    void updateUser(Long id, User user);
}
