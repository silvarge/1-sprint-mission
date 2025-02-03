package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.validation.UserValidator;
import com.sprint.mission.discodeit.common.validation.Validator;
import com.sprint.mission.discodeit.dto.UserReqDTO;
import com.sprint.mission.discodeit.dto.UserResDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.RegionCode;
import com.sprint.mission.discodeit.enums.UserType;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileUserService implements UserService {
    // DB 대체로 생각함
    private final Validator<User, UserReqDTO> userValidator = new UserValidator();
    private final UserRepository userRepository;

    public FileUserService(Path directory) {
        this.userRepository = new FileUserRepository(directory);
    }


    @Override
    public Long createUserData(String username, String nickname, String email, String password, String regionCode, String phone, String imgPath) {
        // 유저 생성 로직
        try {
            UserReqDTO userReqDTO = UserReqDTO.builder()
                    .userName(username)
                    .nickname(nickname)
                    .email(email)
                    .password(password)
                    .userType(UserType.COMMON)
                    .regionCode(RegionCode.fromString(regionCode))
                    .phone(phone)
                    .imgPath(StringUtils.isBlank(imgPath) ? "defaultImg.png" : imgPath)
                    .introduce("")
                    .build();

            userValidator.validateCreate(userReqDTO);

            User user = new User(userReqDTO);  // 유저 생성
            return userRepository.saveUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Custom Exception으로 바꾸기~
    @Override
    public UserResDTO getUser(Long id) {
        User user = Objects.requireNonNull(userRepository.loadUser(id), "해당 ID의 사용자가 존재하지 않습니다.");
        return UserResDTO.builder()
                .id(id)
                .uuid(user.getId())
                .username(user.getUserName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }

    public UserResDTO getUser(String userName) {
        Map.Entry<Long, User> userData = findUserByUserName(userName);
        User user = userData.getValue();

        if (user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        return UserResDTO.builder()
                .id(userData.getKey())
                .uuid(user.getId())
                .username(user.getUserName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }

    @Override
    public List<UserResDTO> getAllUser() {
        return userRepository.loadAllUsers().entrySet().stream()
                .map(entry -> UserResDTO.builder()
                        .id(entry.getKey())
                        .uuid(entry.getValue().getId())
                        .username(entry.getValue().getUserName())
                        .nickname(entry.getValue().getNickname())
                        .email(entry.getValue().getEmail())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public User findUserById(Long id) {
        return userRepository.loadUser(id);
    }

    // userName으로 User 객체와 해당 Long ID 반환
    public Map.Entry<Long, User> findUserByUserName(String userName) {
        return userRepository.loadAllUsers().entrySet().stream()
                .filter(entry -> entry.getValue().getUserName().getName().equals(userName))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }


    @Override
    public boolean updateUser(Long id, UserReqDTO updateDto) {
        boolean isUpdated = false;
        try {
            User updatedUser = new UserValidator().validateUpdate(findUserById(id), updateDto);

            userRepository.updateUser(id, updatedUser); // DB에 반영
            return isUpdated;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserResDTO deleteUser(Long id) {
        UserResDTO deleteUser = getUser(id);
        userRepository.deleteUser(id);
        return deleteUser;
    }

    @Override
    public UserResDTO deleteUser(String userName) {
        UserResDTO deleteUser = getUser(userName);
        userRepository.deleteUser(deleteUser.id());
        return deleteUser;
    }
}