package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.common.CustomApiResponse;
import com.sprint.mission.discodeit.docs.UserControllerDocs;
import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.dto.UserStatusDTO;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public CustomApiResponse<UserDTO.idResponse> createUser(
            @RequestPart("user") UserDTO.request reqUserDto,
            @RequestPart(value = "file", required = false) MultipartFile profile
    ) {
        return CustomApiResponse.created(userService.create(reqUserDto, profile));
    }

    @RequestMapping(method = RequestMethod.GET)
    public CustomApiResponse<List<UserDTO.response>> getAllUsers() {
        return CustomApiResponse.ok(userService.findAll());
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public CustomApiResponse<UserDTO.response> getUser(@PathVariable Long userId) {
        return CustomApiResponse.ok(userService.find(userId));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomApiResponse<UserDTO.idResponse> updateUser(
            @PathVariable Long userId,
            @RequestPart("update") UserDTO.request reqUserDto,
            @RequestPart(value = "file", required = false) MultipartFile updateProfile
    ) {
        return CustomApiResponse.ok(userService.update(UserDTO.update.builder()
                .id(userId)
                .userReqDTO(reqUserDto)
                .profile(updateProfile)
                .build()
        ));
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public CustomApiResponse<UserDTO.idResponse> deleteUser(@PathVariable Long userId) {
        return CustomApiResponse.ok(userService.delete(userId));
    }

    // 사용자의 온라인 상태 업데이트 (UserStatus)
    // - 현재는 업데이트만 존재하기에 User에 넣어둠 (확장 시 따로 분리 가능)
    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PUT)
    public CustomApiResponse<UserStatusDTO.idResponse> updateUserStatus(@PathVariable Long userId, @RequestParam("accessAt") Instant accessAt) {
        UUID userUUID = userService.find(userId).uuid();
        Long statusId = userStatusService.findByUserId(userUUID).id();
        return CustomApiResponse.ok(userStatusService.update(UserStatusDTO.update.builder()
                .userId(userUUID)
                .id(statusId)
                .accessedAt(accessAt)
                .build()
        ));
    }
}
