package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDTO;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserViewController {
    private final UserService userService;

    @GetMapping("/userView")
    public String getUserList(Model model) {
        List<UserDTO.viewResponse> users = userService.findAll().stream()
                .map(user -> {
                    return UserDTO.viewResponse.builder()
                            .id(user.id())
                            .uuid(user.uuid())
                            .username(user.username())
                            .nickname(user.nickname())
                            .email(user.email())
                            .online(user.online())
                            .build();
                }).collect(Collectors.toList());
        model.addAttribute("users", users);
        return "user-list"; // `src/main/resources/templates/user-list.html` 렌더링
    }
}

