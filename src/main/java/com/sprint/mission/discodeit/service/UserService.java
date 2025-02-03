package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserReqDTO;
import com.sprint.mission.discodeit.dto.UserResDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    // 유일한 지 찾는거

    // 사용자 생성
    Long createUserData(String username, String nickname, String email, String password, String regionCode, String phone, String imgPath);

    // 사용자 조회
    UserResDTO getUser(Long id);

    UserResDTO getUser(String userName);

    List<UserResDTO> getAllUser();

    User findUserById(Long id);

    Map.Entry<Long, User> findUserByUserName(String userName);

    // 사용자 정보 업데이트
    // 비밀번호랑 Active 수정은 따로 빼기
    boolean updateUser(Long id, UserReqDTO updateInfo);

    // 사용자 삭제
    UserResDTO deleteUser(Long id);

    UserResDTO deleteUser(String userName);
}
