package com.sprint.mission.discodeit.dto;

/* # UserResDTO
 * - 사용자 응답 전달 객체
 * -- 응답 전달할 때
 * -- 완전 기본, 추가 필요하면 또 추가해야지
 */

import com.sprint.mission.discodeit.common.Email;
import com.sprint.mission.discodeit.common.Name;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResDTO(Long id, UUID uuid, Name username, Name nickname, Email email) {
}
