package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.dto.user.UserSignupRequestDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.util.validation.UserValidator;
import com.sprint.mission.discodeit.util.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorUtils {
    @Bean
    public Validator<User, UserSignupRequestDto, UserUpdateDto> userValidator() {
        return new UserValidator();
    }
}
