package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserSignupRequestDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.HibernateStatisticsService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    @Qualifier("userValidator")
    private final Validator<User, UserSignupRequestDto, UserUpdateDto> userValidator;

    private final UserMapper userMapper;

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentService binaryContentService;

    private final HibernateStatisticsService hibernateStatisticsService;

    @Transactional
    @Override
    public UserResponseDto create(UserSignupRequestDto userReqDto, MultipartFile profile) throws IOException {
        // 유저 생성 로직
        // 유효성 검사
        userValidator.validateCreate(userReqDto);
        // 중복 검사
        if (userRepository.existsUserByEmail(userReqDto.email()) || userRepository.existsUserByUsername(userReqDto.username())) {
            throw new CustomException(ErrorCode.USER_IS_ALREADY_EXIST);
        }

        // user 생성
        User user = userMapper.toEntity(userReqDto);
        UserStatus userStatus = new UserStatus(Instant.now(), user);
        user.updateUserStatus(userStatus);

        // 프로필 이미지 존재 시 생성
        if (profile != null && !profile.isEmpty()) {
            BinaryContentResponseDto profileDto = binaryContentService.create(profile);
            user.updateProfile(binaryContentRepository.findById(profileDto.id()));
        }

        User savedUser = userRepository.saveAndFlush(user);

        hibernateStatisticsService.printStatistics();

        return userMapper.toResponseDto(userRepository.findById(savedUser.getId()));
    }

    @Override
    public UserResponseDto find(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        hibernateStatisticsService.printStatistics();
        return userMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<UserResponseDto> users = userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());

        hibernateStatisticsService.printStatistics(); // Hibernate Statistics 로그 출력

        return users;
//        return userRepository.findAll().stream().map(userMapper::toResponseDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserResponseDto update(UUID userId, UserUpdateDto userUpdateDto, MultipartFile updateProfile) {
        try {
            User updatedUser = userValidator.validateUpdate(userRepository.findById(userId), userUpdateDto);
            if (updatedUser == null) {
                throw new CustomException(ErrorCode.USER_UPDATE_DATA_NOT_FOUND);
            }

            if (updateProfile != null) {
                // 프로필 데이터 존재 여부
                if (updatedUser.getProfile() != null) {
                    binaryContentRepository.delete(updatedUser.getProfile());
                }
                BinaryContentResponseDto updateFile = binaryContentService.create(updateProfile);
                updatedUser.updateProfile(binaryContentRepository.findById(updateFile.id()));
            }

            userRepository.save(updatedUser); // DB에 반영

            hibernateStatisticsService.printStatistics(); // Hibernate Statistics 로그 출력

            return userMapper.toResponseDto(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public UserResponseDto delete(UUID id) {
        User deleteUser = userRepository.findById(id);
        userRepository.delete(deleteUser);

        hibernateStatisticsService.printStatistics(); // Hibernate Statistics 로그 출력

        return userMapper.toResponseDto(deleteUser);
    }
}
