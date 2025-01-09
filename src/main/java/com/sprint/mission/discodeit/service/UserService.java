package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface UserService {

    // 중복 확인
    public void checkDupEmail();
    public void checkDupPhone();
    public void checkDupUserName();
    public void checkDupPassword();

    // 사용자 생성(저장)
    public User storeUserData();

    // 사용자 조회
    public void getUser(UUID uuid);
    public void getUser(String userName);
    public void getAllUser();   // TODO: Paging 넣어 말아?

    // 사용자 정보 업데이트
    public void updateUser(User user, User newUser);

    // 사용자 삭제
    public void deleteUser(User user);
}
