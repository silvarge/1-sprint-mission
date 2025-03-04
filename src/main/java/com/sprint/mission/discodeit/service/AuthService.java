package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;

public interface AuthService {
    UserResponseDto login(AuthDTO.loginReq loginDTO);
}
