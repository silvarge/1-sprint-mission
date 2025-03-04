package com.sprint.mission.discodeit.user;

import com.sprint.mission.discodeit.common.Phone;
import com.sprint.mission.discodeit.dto.common.CommonResponseDto;
import com.sprint.mission.discodeit.dto.user.UserSignupRequestDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.util.EntryUtils;
import com.sprint.mission.discodeit.util.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);

    @InjectMocks
    private BasicUserService userService;
    @Mock
    private Validator<User, UserSignupRequestDto, UserUpdateDto> userValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;

    @DisplayName("회원 생성 테스트")
    @Test
    void createUserTest() {
        UserSignupRequestDto userSignupRequestDto = new UserSignupRequestDto(
                "username", "nickname", "mail@example.com", "!#password2934",
                "010-1111-2222", Phone.RegionCode.KR, User.UserType.COMMON, null
        );

        User user = new User(
                userSignupRequestDto.username(), userSignupRequestDto.nickname(), userSignupRequestDto.email(),
                userSignupRequestDto.password(), userSignupRequestDto.phone(), userSignupRequestDto.regionCode(),
                userSignupRequestDto.userType(), userSignupRequestDto.introduce()
        );

        // Mock 설정
        when(userRepository.save(any(User.class))).thenReturn(1L);
        when(userRepository.load(1L)).thenReturn(user);
        doNothing().when(userValidator).validateCreate(userSignupRequestDto);

        // When
        CommonResponseDto response = userService.create(userSignupRequestDto, null);

        // Then
        Assertions.assertNotNull(response);
        log.info("회원 생성 응답: {}", response.toString());
        log.info("회원 정보: nickname={}", userRepository.load(1L).getNickname());
    }

    @DisplayName("회원 정보 수정 테스트")
    @Test
    void updateUserTest() {
        // given
        // 기존 회원 데이터
        UserSignupRequestDto userSignupRequestDto = new UserSignupRequestDto(
                "username", "nickname", "mail@example.com", "!#password2934",
                "010-1111-2222", Phone.RegionCode.KR, User.UserType.COMMON, null
        );

        User user = new User(
                userSignupRequestDto.username(), userSignupRequestDto.nickname(), userSignupRequestDto.email(),
                userSignupRequestDto.password(), userSignupRequestDto.phone(), userSignupRequestDto.regionCode(),
                userSignupRequestDto.userType(), userSignupRequestDto.introduce()
        );

        // Mock 설정
        when(userRepository.load(1L)).thenReturn(user);
        when(userRepository.load(user.getPublicId())).thenReturn(EntryUtils.of(user.getId(), user));

        log.info("수정 전 데이터: username={}", userRepository.load(1L).getUserName());

        // 업데이트 DTO
        UserUpdateDto userUpdateDto = new UserUpdateDto(
                "newUsername", null, null, null, null, null, "새로운 자기소개"
        );

        // When
        CommonResponseDto response = userService.update(userRepository.load(1L).getPublicId(), userUpdateDto, null);

        // Then
        Assertions.assertNotNull(response);
        log.info("유저 갱신 응답: {}", response.toString());
        log.info("수정 후 데이터: username={}", userRepository.load(1L).getUserName());
    }

}
