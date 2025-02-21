package com.sprint.mission.discodeit.docs;

import com.sprint.mission.discodeit.common.CustomApiResponse;
import com.sprint.mission.discodeit.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Users", description = "User 관련 API")
public interface UserControllerDocs {

    @Operation(summary = "유저 정보 저장", description = "유저 정보를 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보 저장 성공"),
            @ApiResponse(responseCode = "409", description = "유저 정보 저장 실패(유저 중복)")})
    public CustomApiResponse<UserDTO.idResponse> createUser(
            @RequestPart("user") UserDTO.request reqUserDto,
            @RequestPart(value = "file", required = false) MultipartFile profile
    );

}
