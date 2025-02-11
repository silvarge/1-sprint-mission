package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDTO;

public interface AuthService {
    UserDTO.response login(String username, String password);
}
