package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.enums.RegionCode;
import com.sprint.mission.discodeit.enums.UserType;
import lombok.Builder;

/* # UserReqDTO
 * - 사용자 Req DTO
 * -- 무언가 사용자 관련 요청 시 (주로 생성 요청)
 * -- 객체 생성의 책임
 */

/**
 * @param regionCode 문자열로 전달 후 내부에서 변환
 */
@Builder
public record UserReqDTO(String userName, String nickname, String email, String password, UserType userType,
                         RegionCode regionCode,
                         String phone, String imgPath, String introduce) {
}