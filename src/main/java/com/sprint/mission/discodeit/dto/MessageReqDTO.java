package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.Objects;

public class MessageReqDTO implements MessageDTO{
    private User author;
    private Channel channel;
    private String content;

    public MessageReqDTO(User author, Channel channel, String content){
        this.author = Objects.requireNonNull(author, "Author cannot be null");
        this.channel = Objects.requireNonNull(channel, "Channel cannot be null");
        this.content = Objects.requireNonNull(content, "Content cannot be null");
    }

    public User getAuthor() {
        return author;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public String getAuthorName() {
        return author.getUserName().getName();
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getMessageChannel() {     // UUID
        return channel.getId().toString();
    }
}
