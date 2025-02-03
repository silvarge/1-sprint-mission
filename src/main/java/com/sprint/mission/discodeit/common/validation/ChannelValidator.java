package com.sprint.mission.discodeit.common.validation;

import com.sprint.mission.discodeit.dto.ChannelReqDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelValidator implements Validator<Channel, ChannelReqDTO> {
    @Override
    public void validateCreate(ChannelReqDTO entity) {
        if (entity.owner() == null) {
            throw new CustomException(ErrorCode.OWNER_CANNOT_BLANK);
        }

        if (entity.serverName() == null) {
            throw new CustomException(ErrorCode.SERVERNAME_CANNOT_BLANK);
        }
    }

    @Override
    public Channel validateUpdate(Channel current, ChannelReqDTO update) {
        boolean isUpdated = false;
        if (update.owner() != null && !current.getOwner().getUserName().getName().equals(update.owner().getUserName().getName())) {
            current.updateOwner(update.owner());
            isUpdated = true;
        }

        if (update.serverName() != null && !current.getServerName().getName().equals(update.serverName())) {
            current.updateServerName(update.serverName());
            isUpdated = true;
        }

        if (update.description() != null && !current.getDescription().equals(update.description())) {
            current.updateDescription(update.description());
            isUpdated = true;
        }

        if (update.iconImgPath() != null && !current.getIconImgPath().equals(update.iconImgPath())) {
            current.updateIconImgPath(update.iconImgPath());
            isUpdated = true;
        }
        return isUpdated ? current : null;
    }
}
