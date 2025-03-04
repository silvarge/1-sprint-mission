package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.common.CustomApiResponse;
import com.sprint.mission.discodeit.docs.UserControllerDocs;
import com.sprint.mission.discodeit.dto.common.CommonResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserSignupRequestDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// TODO: Mapping시 /api/v1/users 처럼 버전 추가 (확장성을 위함)
// -> 프론트 코드,,, 내가 고치고 싶지 않아서 일단 지금은 냅두는 것

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public CustomApiResponse<CommonResponseDto> createUser(
            @RequestPart("user") UserSignupRequestDto reqUserDto,
            @RequestPart(value = "file", required = false) MultipartFile profile
    ) {
        return CustomApiResponse.created(userService.create(reqUserDto, profile));
    }

    @GetMapping
    public CustomApiResponse<List<UserResponseDto>> getAllUsers() {
        return CustomApiResponse.ok(userService.findAll());
    }

    @GetMapping("/{userId}")
    public CustomApiResponse<UserResponseDto> getUser(@PathVariable Long userId) {
        return CustomApiResponse.ok(userService.find(userId));
    }

    @PutMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomApiResponse<CommonResponseDto> updateUser(
            @PathVariable UUID publicUserId,
            @RequestPart("update") UserUpdateDto userUpdateDto,
            @RequestPart(value = "file", required = false) MultipartFile updateProfile
    ) {
        return CustomApiResponse.ok(userService.update(publicUserId, userUpdateDto, updateProfile));
    }


    @DeleteMapping(path = "/{userId}")
    public CustomApiResponse<CommonResponseDto> deleteUser(@PathVariable Long userId) {
        return CustomApiResponse.ok(userService.delete(userId));
    }

    // 사용자의 온라인 상태 업데이트 (UserStatus)
    // - 현재는 업데이트만 존재하기에 User에 넣어둠 (확장 시 따로 분리 가능)
    @PutMapping(path = "/{userId}/status")
    public CustomApiResponse<CommonResponseDto> updateUserStatus(@PathVariable Long userId, @RequestParam("accessAt") Instant accessAt) {
        UUID userUUID = userService.find(userId).publicId();
        return CustomApiResponse.ok(null);
//        return CustomApiResponse.ok(userStatusService.update(userStatusService.findByUserId(userUUID).id(), userUUID, accessAt));
    }
}
