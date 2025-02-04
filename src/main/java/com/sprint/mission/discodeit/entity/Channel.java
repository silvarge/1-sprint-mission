package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.Name;
import com.sprint.mission.discodeit.dto.ChannelReqDTO;
import com.sprint.mission.discodeit.enums.ChannelType;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    /**
     * TODO: 서버별 프로필이 존재하는데 User 객체를 넣어도 될까? UUID를 넣는게 나을까?
     * members, bannedUser도 같음.
     * members - 역할, 가입 시간, 서버 닉네임, UUID
     * bannedUser - UUID, 차단된 이유, 차단 기간?
     */
    private UUID id;    // 고유 번호
    private User owner;     // 서버 주인
    private Name serverName;    // 서버명
    private String description; // 서버 소개
    private ChannelType channelType;
    private String iconImgPath;   // 채널 아이콘 이미지
    private List<User> members;     // 채널 가입자 리스트 - 근데 이건 나중에 객체로 빼는게 좋지 않을까..? 관리하기가 좀
    private List<User> bannedUser;  // 차단 사용자 리스트 - 근데 이건 나중에 객체로 빼는게 좋지 않을까..?
    private boolean status;     // 서버가 활성 상태인지 비활성화 상태인지 상태
    private Instant createdAt;
    private Instant updatedAt;

    // 생성자
    public Channel(ChannelReqDTO channelReqDTO) {
        id = UUID.randomUUID();
        createdAt = Instant.now();
        this.owner = channelReqDTO.owner();
        this.serverName = new Name(channelReqDTO.serverName());
        this.channelType = channelReqDTO.channelType();
        this.description = channelReqDTO.description();
        this.iconImgPath = channelReqDTO.iconImgPath();
        members = new ArrayList<>();
        bannedUser = new ArrayList<>();
        this.status = true;  // 기본 생성 시 활성화 상태
    }

    // 중복 확인

    // Setter (update)
    public void updateOwner(User user) {
        this.owner = user;
        setUpdatedAt();
    }

    public void updateServerName(String name) {
        this.serverName.setName(name);
        setUpdatedAt();
    }

    public void updateDescription(String content) {
        this.description = content;
        setUpdatedAt();
    }

    public void updateIconImgPath(String imgPath) {
        this.iconImgPath = imgPath;
        setUpdatedAt();
    }

    public void updateStatus(boolean status) {
        this.status = status;
        setUpdatedAt();
    }

    public void addMember(User member) {
        this.members.add(member);
        setUpdatedAt();
    }

    public void removeMember(User member) {
        this.members.remove(member);
        setUpdatedAt();
    }

    public void addBannedUser(User bannedUser) {
        this.bannedUser.add(bannedUser);
        setUpdatedAt();
    }

    public void removeBannedUser(User bannedUser) {
        this.bannedUser.remove(bannedUser);
        setUpdatedAt();
    }

    void setUpdatedAt() {
        updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Channel {" +
                "id=" + id +
                ", owner=" + owner +
                ", serverName=" + serverName +
                ", description='" + description + '\'' +
                '}';
    }
}
