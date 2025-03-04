package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.common.Phone;
import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

@Builder
public record UserRequestDto(String username, String nickname, String email, Phone.RegionCode regionCode, String phone,
                             User.UserType userType,
                             String introduce) {
}
