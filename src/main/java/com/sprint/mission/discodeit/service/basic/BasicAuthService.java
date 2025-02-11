package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public UserDTO.response login(String username, String password) {
        if (userRepository.confirmLogin(username, password)) {
            Map.Entry<Long, User> user = userRepository.findUserByUserName(username);

            return UserDTO.response.builder()
                    .id(user.getKey())
                    .uuid(user.getValue().getId())
                    .username(user.getValue().getUserName())
                    .email(user.getValue().getEmail())
                    .nickname(user.getValue().getNickname())
                    .build();
        }
        // TODO: ErrorCode 추가 필요
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
}
