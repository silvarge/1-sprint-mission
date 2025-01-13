package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.UUID;

public class ChannelResDTO implements ChannelDTO {
    private Long id;
    private String uuid;
    private String ownerName;
    private String serverName;
    private String description;
    private String iconImgPath;

    public ChannelResDTO(Long id, Channel channel){
        this.id = id;
        this.uuid = channel.getId().toString();
        this.ownerName = channel.getOwner().getUserName().getName();
        this.serverName = channel.getServerName().getName();
        this.description = channel.getDescription();
        this.iconImgPath = channel.getIconImgPath();
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
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

    public String getUuid() {
        return uuid;
    }
}
