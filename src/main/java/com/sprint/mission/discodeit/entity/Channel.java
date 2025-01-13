package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.ServerName;
import com.sprint.mission.discodeit.dto.ChannelReqDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    /**
     * TODO: 서버별 프로필이 존재하는데 User 객체를 넣어도 될까? UUID를 넣는게 나을까?
     * members, bannedUser도 같음.
     * members - 역할, 가입 시간, 서버 닉네임, UUID
     * bannedUser - UUID, 차단된 이유, 차단 기간?
     */
    private UUID id;    // 고유 번호
    private User owner;     // 서버 주인
    private ServerName serverName;    // 서버명
    private String description; // 서버 소개
    private String iconImgPath;   // 채널 아이콘 이미지
    private List<User> members;     // 채널 가입자 리스트 - 근데 이건 나중에 객체로 빼는게 좋지 않을까..? 관리하기가 좀
    private List<User> bannedUser;  // 차단 사용자 리스트 - 근데 이건 나중에 객체로 빼는게 좋지 않을까..?
    private boolean status;     // 서버가 활성 상태인지 비활성화 상태인지 상태
    private long createdAt;
    private long updatedAt;

    // 생성자
    public Channel(ChannelReqDTO channelReqDTO) {
        id = UUID.randomUUID();
        createdAt = System.currentTimeMillis();
        this.owner = channelReqDTO.getOwner();
        this.serverName = new ServerName(channelReqDTO.getServerName());
        this.description = channelReqDTO.getDescription();
        this.iconImgPath = channelReqDTO.getIconImgPath();
        members = new ArrayList<User>();
        bannedUser = new ArrayList<User>();
        status = true;  // 기본 생성 시 활성화 상태
    }

    // 중복 확인

    // Setter (update)
    public void updateOwner(User user){
        this.owner = user;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateServerName(String name){
        if(this.serverName.setServerName(name)) {
            this.updatedAt = System.currentTimeMillis();
        }else{
            throw new IllegalArgumentException("잘못된 형식입니다.");
        }
    }

    public void updateDescription(String content) {
        this.description = content;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateIconImgPath(String imgPath){
        this.iconImgPath = imgPath;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateStatus(boolean status){
        this.status = status;
        this.updatedAt = System.currentTimeMillis();
    }

    public void addMember(User member){
        this.members.add(member);
        this.updatedAt = System.currentTimeMillis();
    }

    public void removeMember(User member){
        this.members.remove(member);
        this.updatedAt = System.currentTimeMillis();
    }

    public void addBannedUser(User bannedUser){
        this.bannedUser.add(bannedUser);
        this.updatedAt = System.currentTimeMillis();
    }

    public void removeBannedUser(User bannedUser){
        this.bannedUser.remove(bannedUser);
        this.updatedAt = System.currentTimeMillis();
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getIconImgPath() {
        return iconImgPath;
    }

    public String getDescription() {
        return description;
    }

    public User getOwner() {
        return owner;
    }

    public ServerName getServerName() {
        return serverName;
    }

    public List<User> getBannedUser() {
        return bannedUser;
    }

    public List<User> getMembers() {
        return members;
    }

    public boolean isStatus() {
        return status;
    }


}
