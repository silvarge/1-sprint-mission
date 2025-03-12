package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsUserByEmail(String email);

    boolean existsUserByUsername(String username);

    User findByUsername(String username);

    boolean existsUserByUsernameAndPassword(String username, String password);
}