package com.sprint.mission.discodeit.dto;

public class MessageUpdateDTO {
    private String content;

    public MessageUpdateDTO(String content){
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
