package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.common.Name;
import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponseDto(UUID publicId, Name nickname, boolean online) {
    public static UserResponseDto from(User user, boolean isOnline) {
        return UserResponseDto.builder()
                .publicId(user.getPublicId())
                .nickname(user.getNickname())
                .online(isOnline)
                .build();
    }
}
