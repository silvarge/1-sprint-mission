package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.Email;
import com.sprint.mission.discodeit.common.Name;
import com.sprint.mission.discodeit.common.Password;
import com.sprint.mission.discodeit.common.Phone;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/* # User
 * - 사용자 객체
 * -- 객체 생성의 책임
 */

@Getter
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 추후 JPA (DB) 도입 시 Entity 어노테이션 및 ID 어노테이션을 통해 사라질 친구
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    private Long id;
    private UUID publicId;    // 고유 번호
    private Name userName;  // ID와 같은 역할
    private Name nickname;  // 별명 (사용자)
    private Email email;    // 이메일
    private transient Password password;    // 비밀번호 -> 추후에는 암호화 되어 저장된다고 생각하기
    private Phone phone;    // 전화 번호
    private UserType userType;  // 기본은 Common
    private boolean status; // 활성화(1)/비활성화(0) 상태 -> 생성 시 default로 1
    private String introduce;   // 자기 소개 (Nullable)
    private Instant createdAt; // 생성 시간
    private Instant updatedAt; // 업데이트 시간
    // 닉네임 등 서버 내 설정은 Profile로 따로 빼야 함

    // 생성자
    public User(
            String username, String nickname, String email, String password,
            String phone, Phone.RegionCode regionCode, UserType userType, String introduce
    ) {
        this.id = ID_GENERATOR.getAndIncrement();
        this.publicId = UUID.randomUUID();
        this.userName = new Name(username);
        this.nickname = new Name(nickname);
        this.email = new Email(email);
        this.password = new Password(password);
        this.phone = new Phone(phone, regionCode);
        this.userType = userType;    // BOT은 어떻게 생성되는지 모른다.. 일단 COMMON으로 정해둠
        this.status = true;
        this.createdAt = Instant.now();
        refreshUpdatedAt();
        this.introduce = introduce;
    }

    public enum UserType {
        COMMON, // 일반 유저 (Default)
        BOT,    // 봇
        STAFF;   // 관리자

        public static UserType fromString(String value) {
            if (StringUtils.isBlank(value)) {
                throw new IllegalArgumentException("UserType은 null이 될 수 없습니다.");
            }
            try {
                return UserType.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("존재하지 않는 UserType: " + value);
            }
        }
    }

    // Update (뭐 Setter이긴 한데 Update라는 의미를 강조하고 싶음)
    public void updateUserName(String userName) {
        // 사용자명(userName) 업데이트 - 고유한 값이기 때문에 불변 객체로 설정
        this.userName = new Name(userName);
        refreshUpdatedAt();
    }

    public void updateNickname(String nickname) {
        this.nickname.setName(nickname);
        refreshUpdatedAt();
    }

    public void updateEmail(String email) {
        this.email.changeEmailAddr(email);
        refreshUpdatedAt();
    }

    public void updatePhone(String phone, String regionCode) {
        this.phone.setPhone(phone);
        this.phone.setRegionCode(Phone.RegionCode.fromString(regionCode));
        refreshUpdatedAt();
    }

    public void updateUserType(UserType userType) {
        this.userType = userType;
        refreshUpdatedAt();
    }

    public void updateStatus(boolean status) {
        this.status = status;
        refreshUpdatedAt();
    }

    public void updateIntroduce(String introduce) {
        this.introduce = introduce;
        refreshUpdatedAt();
    }

    void refreshUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "ID: " + this.getId().toString() + "\n Username: " + this.getUserName().getName();
    }
}
