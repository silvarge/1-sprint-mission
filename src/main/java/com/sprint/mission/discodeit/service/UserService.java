package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.common.CommonResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserSignupRequestDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // 유일한 지 찾는거

    // 사용자 생성
    CommonResponseDto create(UserSignupRequestDto userReqDto, MultipartFile profile);

    // 사용자 조회
    UserResponseDto find(Long id);

    UserResponseDto find(UUID publicId);

    List<UserResponseDto> findAll();

    // 사용자 정보 업데이트
    // 비밀번호랑 Active 수정은 따로 빼기
    CommonResponseDto update(UUID publicUserId, UserUpdateDto request, MultipartFile updateProfile);

    // 사용자 삭제
    CommonResponseDto delete(Long id);

    CommonResponseDto delete(UUID publicId);
}
