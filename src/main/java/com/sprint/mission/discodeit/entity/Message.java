package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

/**
 * # Message
 * - 텍스트 채팅 객체
 * - 단체 대화만 한다고 가정 (언급 기능 등은 일단 고려하지 않음)
 * - 파일 첨부 기능 등이 아닌 단순 문자 채팅만 한다고 고려
 */

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

    @Column(name = "content", nullable = false)
    private String content; // 메시지 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", foreignKey = @ForeignKey(name = "fk_channel"), nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Channel channel;    // 메시지 대상 서버

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "fk_author"), nullable = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User author;

    // 사용자가 채널에 추가/갱신 시 자동 반영 (삭제 시 수동으로 삭제하는 것이 안전)
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments = new ArrayList<>();
    // 메시지 삭제 시 message_attachments 테이블에서 데이터 삭제 로직 추가해야 함 - 명시적 삭제
    // message.getAttachments().clear();  // message_attachments 테이블에서 삭제

    // 생성자
    public Message(String content, Channel channel, User author) {
        this.content = content;
        this.channel = channel;
        this.author = author;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateAuthor(User author) {
        this.author = author;
    }

    public void updateChannel(Channel channel) {
        this.channel = channel;
    }

    public void clearAttachments() {
        this.attachments.clear();
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                '}';
    }
}
