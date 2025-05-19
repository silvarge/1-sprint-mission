package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserResponseDto(UUID id, String username, String nickname, String email,
                              BinaryContentResponseDto profile,
                              Boolean online) {
//    public static UserResponseDto from(User user, boolean isOnline) {
//        return UserResponseDto.builder()
//                .id(user.getPublicId())
//                .nickname(user.getNickname())
//                .online(isOnline)
//                .build();
//    }
}
