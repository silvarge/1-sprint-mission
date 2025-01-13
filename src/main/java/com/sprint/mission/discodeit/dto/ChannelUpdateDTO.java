package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;

public class ChannelUpdateDTO implements ChannelDTO {
    private User owner;
    private String serverName;
    private String description;
    private String iconImgPath;

    public ChannelUpdateDTO(User owner, String serverName, String description, String iconImgPath){
        this.owner = owner;
        this.serverName = serverName;
        this.description = description;
        this.iconImgPath = iconImgPath;
    }

    public User getOwner() {
        return owner;
    }

    @Override
    public String getOwnerName() {
        return owner.getUserName().getName();
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    public String getIconImgPath() {
        return iconImgPath;
    }

    public String getDescription() {
        return description;
    }
}
