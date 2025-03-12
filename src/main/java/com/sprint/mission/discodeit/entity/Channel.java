package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

    @Column(name = "name", length = 100)
    private String serverName;    // 서버명

    @Column(name = "description", columnDefinition = "text")
    private String description; // 서버 소개

    @Column(name = "channel_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType channelType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "fk_owner"))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User owner;

    // 사용자가 채널에 추가/갱신 시 자동 반영 (삭제 시 수동으로 삭제하는 것이 안전)
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "channel_members",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<User> members = new ArrayList<>();

    // 생성자
    public Channel(String serverName, ChannelType channelType, String description, User owner) {
        this.serverName = serverName;
        this.channelType = channelType;
        this.description = description;
        this.owner = owner;
    }

    public enum ChannelType {
        PUBLIC,
        PRIVATE;

        public static ChannelType fromString(String value) {
            if (StringUtils.isBlank(value)) {
                throw new CustomException(ErrorCode.CHANNEL_TYPE_NOT_FOUND);
            }
            try {
                return ChannelType.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.CHANNEL_TYPE_NOT_FOUND);
            }
        }
    }

    // Setter (update)
    public void updateServerName(String serverName) {
        this.serverName = serverName;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    // TODO: 채널에 접근할 수 있는지 여부 서비스로 빼거나 하는게 좋겠음
//    public boolean canAccessChannel(UUID userId) {
//        return channelType == ChannelType.PUBLIC ||
//                ownerId.equals(userId) ||
//                members.contains(userId);
//    }

    @Override
    public String toString() {
        return "Channel{" +
                "serverName='" + serverName + '\'' +
                ", description='" + description + '\'' +
                ", channelType=" + channelType +
                '}';
    }
}
