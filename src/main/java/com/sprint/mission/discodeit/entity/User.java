package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.UserType;
import com.sprint.mission.discodeit.common.*;

import java.util.UUID;

/* # User
 * - 사용자 객체
 */

public class User {
    private UUID id;    // 고유 번호
    private UserName userName;  // ID와 같은 역할
    private Nickname nickname;  // 별명 (사용자)
    private Email email;    // 이메일
    private Password passwd;    // 비밀번호 -> 추후에는 암호화 되어 저장된다고 생각하기
    private Phone phone;    // 전화 번호 (Nullable)
    private UserType userType;  // 기본은 Common
    private boolean status; // 활성화(1)/비활성화(0) 상태 -> 생성 시 default로 1
    private String userImgPath;  //
    private String introduce;   // 자기 소개 (Nullable)
    private long createdAt; // 생성 시간
    private long updatedAt; // 업데이트 시간
    // 닉네임 등 서버 내 설정은 Profile로 따로 빼야 함

    // 생성자 - 전화번호 - O / 자기소개 X
    public User(String userName, String nickname, String email, String passwd, String regionCode, String phone, String imgPath, String introduce) {
        id = UUID.randomUUID();
        this.userName = new UserName(userName);
        this.nickname = new Nickname(nickname);
        this.email = new Email(email);
        this.passwd = new Password(passwd);
        this.phone = new Phone(phone, regionCode);
        this.userType = UserType.COMMON;    // BOT은 어떻게 생성되는지 모른다
        this.status = true;
        this.userImgPath = imgPath;
        this.introduce = introduce;
        createdAt = System.currentTimeMillis();
    }

    // 전화번호 - O / 자기소개 X
    public User(String userName, String nickname, String email, String passwd, String regionCode, String phone, String imgPath) {
        id = UUID.randomUUID();
        this.userName = new UserName(userName);
        this.nickname = new Nickname(nickname);
        this.email = new Email(email);
        this.passwd = new Password(passwd);
        this.phone = new Phone(phone, regionCode);
        this.userType = UserType.COMMON;
        this.status = true;
        this.userImgPath = imgPath;
        createdAt = System.currentTimeMillis();
    }

    // Update (Setter이긴 한데 Update라는 의미를 강조하고 싶음)
    // TODO: updatedAt -> 내부 공통 메서드 처리 고려하기
    public void updateUserInfo(String userName, String nickname, String email, String phone, String regionCode) {
        if (userName != null) updateUserName(userName);
        if (nickname != null) updateNickname(nickname);
        if (email != null) updateEmail(email);
        if (phone != null) updatePhone(phone, regionCode);
    }

    public void updateUserName(String userName){
        // 사용자명(userName) 업데이트 - 고유한 값이기 때문에 불변 객체로 설정
        this.userName = new UserName(userName);
        updatedAt = System.currentTimeMillis();
    }

    public void updateNickname(String nickname){
        // 닉네임 - 자주 바뀌기 대문에 가변 객체로
        // TODO: 닉네입 변경 요청 시 이전 닉네임과 동일하다면? 굳이 함수를 실행할 필요가 없다는 점 고려
        if(this.nickname.setNickname(nickname)) {
            updatedAt = System.currentTimeMillis();
        }else{
            throw new IllegalArgumentException("잘못된 형식입니다.");
        }
    }

    public void updateEmail(String email){
        this.email = new Email(email);
        updatedAt = System.currentTimeMillis();
    }

    public void updatePhone(String phone, String regionCode){
        this.phone = new Phone(phone, regionCode);
        updatedAt = System.currentTimeMillis();
    }

    public void updateUserType(UserType userType){
        this.userType = userType;
        updatedAt = System.currentTimeMillis();
    }

    public void updateStatus(boolean status){
        this.status = status;
        updatedAt = System.currentTimeMillis();
    }

    public void updateUserImg(String userImgPath){
        this.userImgPath = userImgPath;
        updatedAt = System.currentTimeMillis();
    }

    public void updateIntroduce(){
        this.introduce = introduce;
        updatedAt = System.currentTimeMillis();
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Nickname getNickname() {
        return nickname;
    }

    public UserName getUserName() {
        return userName;
    }

    public UserType getUserType() {
        return userType;
    }

    public Phone getPhone() {
        return phone;
    }

    public String getUserImgPath() {
        return userImgPath;
    }

    public String getIntroduce() {
        return introduce;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }
}
