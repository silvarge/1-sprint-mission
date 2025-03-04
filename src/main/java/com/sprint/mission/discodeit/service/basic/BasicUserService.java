package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.common.CommonResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserSignupRequestDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FileConverter;
import com.sprint.mission.discodeit.util.validation.UserValidator;
import com.sprint.mission.discodeit.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    @Qualifier("userValidator")
    private final Validator<User, UserSignupRequestDto, UserUpdateDto> userValidator;

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public CommonResponseDto create(UserSignupRequestDto userReqDto, MultipartFile profile) {
        // 유저 생성 로직
        try {
            // 유효성 검사
            userValidator.validateCreate(userReqDto);

            // 중복 검사
            if (userRepository.isExistByEmail(userReqDto.email()) || userRepository.isExistByUserName(userReqDto.username())) {
                throw new CustomException(ErrorCode.USER_IS_ALREADY_EXIST);
            }

            // user 생성
            Long userId = userRepository.save(new User(
                    userReqDto.username(), userReqDto.nickname(), userReqDto.email(), userReqDto.password(),
                    userReqDto.phone(), userReqDto.regionCode(), userReqDto.userType(), userReqDto.introduce()
            ));
            UUID userPublicId = userRepository.load(userId).getPublicId();

            // 프로필 이미지 존재 시 생성
            if (profile != null && !profile.isEmpty()) {
                BinaryContentDTO.request convert = FileConverter.convertToBinaryContent(profile, userPublicId, BinaryContent.ContentType.PROFILE);
                binaryContentRepository.save(new BinaryContent(
                        convert.referenceId(), convert.file(), convert.contentType(), convert.mimeType(), convert.filename()
                ));
            }

            // userStatus 생성
            userStatusRepository.save(new UserStatus(userPublicId, Instant.now()));

            return new CommonResponseDto(userPublicId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserResponseDto find(Long id) {
        User user = userRepository.load(id);
        if (user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        Map.Entry<Long, UserStatus> userStatus = userStatusRepository.findUserStatusByUserId(user.getPublicId());

        return UserResponseDto.from(user, userStatus.getValue().isOnline());
    }

    @Override
    public UserResponseDto find(UUID publicId) {
        Map.Entry<Long, User> userData = userRepository.load(publicId);
        User user = userData.getValue();

        if (user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);
        UserStatus userStatus = userStatusRepository.findUserStatusByUserId(user.getPublicId()).getValue();

        return UserResponseDto.from(user, userStatus.isOnline());
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.loadAll().values().stream()
                .map(user -> {
                            Map.Entry<Long, UserStatus> userStatus = userStatusRepository.findUserStatusByUserId(user.getPublicId());
                            return UserResponseDto.from(user, userStatus.getValue().isOnline());
                        }
                )
                .collect(Collectors.toList());
    }

    @Override
    public CommonResponseDto update(UUID publicUserId, UserUpdateDto userUpdateDto, MultipartFile updateProfile) {
        try {
            User updatedUser = new UserValidator().validateUpdate(userRepository.load(publicUserId).getValue(), userUpdateDto);
            if (updatedUser == null) {
                throw new CustomException(ErrorCode.USER_UPDATE_DATA_NOT_FOUND);
            }

            if (updateProfile != null) {
                // 프로필 데이터 존재 여부
                if (binaryContentRepository.hasBinaryContent(updatedUser.getPublicId())) {
                    // 존재 시 삭제
                    Map.Entry<Long, BinaryContent> binaryContent = binaryContentRepository.findProfileImageByMessageId(updatedUser.getPublicId());
                    binaryContentRepository.delete(binaryContent.getKey());
                }
                // 새로운 데이터 생성
                BinaryContentDTO.request file = FileConverter.convertToBinaryContent(updateProfile, publicUserId, BinaryContent.ContentType.PROFILE);
                binaryContentRepository.save(new BinaryContent(
                        file.referenceId(), file.file(), file.contentType(), file.mimeType(), file.filename()
                ));
            }
            userRepository.update(updatedUser.getId(), updatedUser); // DB에 반영
            return CommonResponseDto.from(updatedUser.getPublicId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CommonResponseDto delete(Long id) {
        User deleteUser = userRepository.load(id);

        binaryContentRepository.deleteAllFileByReferenceId(deleteUser.getPublicId());
        userStatusRepository.deleteByUserId(deleteUser.getPublicId());

        userRepository.delete(id);
        return CommonResponseDto.from(deleteUser.getPublicId());
    }

    @Override
    public CommonResponseDto delete(UUID publicId) {
        User deleteUser = userRepository.load(publicId).getValue();

        binaryContentRepository.deleteAllFileByReferenceId(deleteUser.getPublicId());
        userStatusRepository.deleteByUserId(deleteUser.getPublicId());

        userRepository.delete(deleteUser.getId());
        return CommonResponseDto.from(deleteUser.getPublicId());
    }
}
