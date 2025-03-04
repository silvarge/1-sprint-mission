package com.sprint.mission.discodeit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.Phone;
import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.user.UserSignupRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @DisplayName("createUser Controller 입력 테스트")
    @Test
    void createUserCortrollerRequestTest() throws Exception {
        // given
        UserSignupRequestDto userSignupRequestDto = new UserSignupRequestDto(
                "username", "nickname", "aaa@example.com", "!#passWord136",
                "010-0000-1123", Phone.RegionCode.KR, User.UserType.COMMON, null);

        String requestJson = new ObjectMapper().writeValueAsString(userSignupRequestDto);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").exists());
    }
}
