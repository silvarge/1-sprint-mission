package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;

import java.util.Objects;

public class ChannelReqDTO implements ChannelDTO{
    private User owner;
    private String serverName;
    private String description;
    private String iconImgPath;

    public ChannelReqDTO(User owner, String serverName, String description, String iconImgPath){
        this.owner = Objects.requireNonNull(owner, "Owner cannot be null");
        this.serverName = Objects.requireNonNull(serverName, "ServerName cannot be null");
        this.description = Objects.requireNonNullElse(description, "");
        this.iconImgPath = Objects.requireNonNullElse(iconImgPath, "defaultServerIconImg.png");
    }

    @Override
    public String getOwnerName() {
        return owner.getUserName().getName();
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    public User getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public String getIconImgPath() {
        return iconImgPath;
    }
}
