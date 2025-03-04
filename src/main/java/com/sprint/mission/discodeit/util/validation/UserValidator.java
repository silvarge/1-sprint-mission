package com.sprint.mission.discodeit.util.validation;

import com.sprint.mission.discodeit.common.Phone;
import com.sprint.mission.discodeit.dto.user.UserSignupRequestDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

@Component("userValidator")
public class UserValidator implements Validator<User, UserSignupRequestDto, UserUpdateDto> {
    @Override
    public void validateCreate(UserSignupRequestDto entity) {
        if (!ValidatorExp.USERNAME.matches(entity.username())) {
            throw new CustomException(ErrorCode.INVALID_USERNAME);
        }

        if (!ValidatorExp.NICKNAME.matches(entity.nickname())) {
            throw new CustomException(ErrorCode.INVALID_NICKNAME);
        }

        if (!ValidatorExp.PASSWORD.matches(entity.password())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        if (!ValidatorExp.PHONE.matches(entity.phone())) {
            throw new CustomException(ErrorCode.INVALID_PHONENUM);
        }

        if (!ValidatorExp.EMAIL.matches(entity.email())) {
            throw new CustomException(ErrorCode.INVALID_EMAIL);
        }

        if (StringUtils.isBlank(entity.regionCode().name())) {
            throw new CustomException(ErrorCode.REGION_CODE_IS_NOT_NULL);
        }
    }

    @Override
    public User validateUpdate(User current, UserUpdateDto update) {
        boolean isUpdated = false;
        if (update.username() != null && !update.username().equals(current.getUserName().getName()) && ValidatorExp.USERNAME.matches(update.username())) {
            current.updateUserName(update.username());
            isUpdated = true;
        }

        if (update.nickname() != null && !update.nickname().equals(current.getNickname().getName()) && ValidatorExp.NICKNAME.matches(update.nickname())) {
            current.updateNickname(update.nickname());
            isUpdated = true;
        }

        if (update.email() != null && !update.email().equals(current.getEmail().getEmail()) && ValidatorExp.EMAIL.matches(update.email())) {
            current.updateEmail(update.email());
            isUpdated = true;
        }

        if (update.userType() != null && (update.userType() != User.UserType.fromString(current.getUserType().toString().toUpperCase()))) {
            current.updateUserType(User.UserType.fromString(update.userType().toString().toUpperCase()));
            isUpdated = true;
        }

        // phone
        if ((update.regionCode() != null && update.phone() != null)
                && (!update.phone().equals(current.getPhone().getPhone()) || update.regionCode() != Phone.RegionCode.fromString(current.getPhone().getRegionCode().toString().toUpperCase()))
                && ValidatorExp.PHONE.matches(update.phone())) {
            current.updatePhone(update.phone(), update.regionCode().toString().toUpperCase());
            isUpdated = true;
        }

        if (update.introduce() != null && !update.introduce().equals(current.getIntroduce())) {
            current.updateIntroduce(update.introduce());
            isUpdated = true;
        }
        return isUpdated ? current : null;
    }
}
