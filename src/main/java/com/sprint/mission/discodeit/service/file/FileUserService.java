package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.validation.Validator;
import com.sprint.mission.discodeit.common.validation.ValidatorImpl;
import com.sprint.mission.discodeit.dto.UserReqDTO;
import com.sprint.mission.discodeit.dto.UserResDTO;
import com.sprint.mission.discodeit.dto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.RegionCode;
import com.sprint.mission.discodeit.enums.UserType;
import com.sprint.mission.discodeit.exception.CustomException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.service.UserService;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FileUserService implements UserService {
    // DB 대체로 생각함
    private final Validator validator = new ValidatorImpl();
    private final Path directory;
    private final AtomicLong idGenerator;

    public FileUserService(Path directory) {
        this.directory = directory;
        init(directory);
        this.idGenerator = new AtomicLong(1);   // ID 초기값 1
    }

    // 파일 입출력 관련 ========================
    // 디렉토리 초기화
    private void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + directory, e);
            }
        }
    }

    // 유저 데이터 저장
    private void saveUser(Long id, User user) {
        Path filePath = directory.resolve(id + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user data: " + id, e);
        }
    }

    // 유저 데이터 로드
    private User loadUser(Long id) {
        Path filePath = directory.resolve(id + ".ser");
        if (!Files.exists(filePath)) {
            throw new RuntimeException("User data not found for ID: " + id);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load user data: " + id, e);
        }
    }

    private Map<Long, User> loadAllUsers() {
        try {
            return Files.list(directory)
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            User user = (User) ois.readObject();
                            Long id = Long.valueOf(path.getFileName().toString().replace(".ser", ""));
                            return Map.entry(id, user);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("Failed to load user data from file: " + path, e);
                        }
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users from directory", e);
        }
    }

    // ID 초기값 계산 (디렉터리 내 파일 개수 기반)
    private long getNextId() {
        try {
            return Files.list(directory).count() + 1;
        } catch (IOException e) {
            throw new RuntimeException("Failed to calculate next ID", e);
        }
    }

    // 파일 입출력 관련 ========================


    @Override
    public Long createUserData(String username, String nickname, String email, String password, String regionCode, String phone, String imgPath) {
        // 유저 생성 로직
        try {
            if (!validator.userNameValidator(username)) {
                throw new CustomException(ErrorCode.INVALID_USERNAME);
            }

            if (!validator.nicknameValidator(nickname)) {
                throw new CustomException(ErrorCode.INVALID_NICKNAME);
            }

            if (!validator.passwordValidator(password)) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }

            if (!validator.phoneNumValidator(phone)) {
                throw new CustomException(ErrorCode.INVALID_PHONENUM);
            }

            if (!validator.emailValidator(email)) {
                throw new CustomException(ErrorCode.INVALID_EMAIL);
            }

            if (StringUtils.isBlank(regionCode)) {
                throw new CustomException(ErrorCode.REGION_CODE_IS_NOT_NULL);
            }

            imgPath = StringUtils.isBlank(imgPath) ? "defaultImg.png" : imgPath;

            User user = new User(new UserReqDTO(username, nickname, email, password, regionCode, phone, imgPath));  // 유저 생성
            Long id = idGenerator.getAndIncrement();    // 1++
            saveUser(id, user);
            return id;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Custom Exception으로 바꾸기~
    @Override
    public UserResDTO getUser(Long id) {
        User user = Objects.requireNonNull(loadUser(id), "해당 ID의 사용자가 존재하지 않습니다.");
        return new UserResDTO(id, user);
    }

    @Override
    public User getUserToUserObj(Long id) {
        return Objects.requireNonNull(loadUser(id), "해당 ID의 사용자가 존재하지 않습니다.");
    }

    @Override
    public UserResDTO getUser(String userName) {
        return loadAllUsers().entrySet().stream()
                .filter(entry -> entry.getValue().getUserName().getName().equals(userName))
                .findFirst()
                .map(entry -> new UserResDTO(entry.getKey(), entry.getValue()))
                .orElseThrow(() -> new RuntimeException("해당 이름의 사용자가 존재하지 않습니다."));
    }

    @Override
    public List<UserResDTO> getAllUser() {
        return loadAllUsers().entrySet().stream()
                .map(entry -> new UserResDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public User findUserById(Long id) {
        return loadUser(id);
    }

    // userName으로 User 객체와 해당 Long ID 반환
    public Optional<Map.Entry<Long, User>> findUserByUserName(String userName) {
        return loadAllUsers().entrySet().stream()
                .filter(entry -> entry.getValue().getUserName().getName().equals(userName))
                .findFirst();
    }

    @Override
    public boolean updateUser(Long id, UserUpdateDTO updateInfo) {
        boolean isUpdated = false;
        try {
            User user = getUserToUserObj(id);
            if (updateInfo.getUserName() != null && !user.getUserName().getName().equals(updateInfo.getUserName()) && validator.userNameValidator(updateInfo.getUserName())) {
                user.updateUserName(updateInfo.getUserName());
                isUpdated = true;
            }

            if (updateInfo.getNickname() != null && !user.getNickname().getName().equals(updateInfo.getNickname()) && validator.nicknameValidator(updateInfo.getNickname())) {
                user.updateNickname(updateInfo.getNickname());
                isUpdated = true;
            }

            if (updateInfo.getEmail() != null && !user.getEmail().getEmail().equals(updateInfo.getEmail()) && validator.emailValidator(updateInfo.getEmail())) {
                user.updateEmail(updateInfo.getEmail());
                isUpdated = true;
            }

            if (updateInfo.getUserType() != null && (user.getUserType() != UserType.fromString(updateInfo.getUserType().toUpperCase()))) {
                user.updateUserType(UserType.fromString(updateInfo.getUserType().toUpperCase()));
                isUpdated = true;
            }

            // phone
            if ((updateInfo.getRegionCode() != null && updateInfo.getPhone() != null)
                    && (!user.getPhone().getPhoneNum().equals(updateInfo.getPhone()) || user.getPhone().getRegionCode() != RegionCode.fromString(updateInfo.getRegionCode().toUpperCase()))
                    && validator.phoneNumValidator(updateInfo.getPhone())) {
                user.updatePhone(updateInfo.getPhone(), updateInfo.getRegionCode().toUpperCase());
                isUpdated = true;
            }

            if (updateInfo.getImgPath() != null && !user.getUserImgPath().equals(updateInfo.getImgPath())) {
                user.updateUserImg(updateInfo.getImgPath());
                isUpdated = true;
            }

            if (updateInfo.getIntroduce() != null && !user.getIntroduce().equals(updateInfo.getIntroduce())) {
                user.updateIntroduce(updateInfo.getIntroduce());
                isUpdated = true;
            }
            saveUser(id, user); // DB에 반영
            return isUpdated;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserResDTO deleteUser(Long id) {
        UserResDTO deleteUser = getUser(id);
        Path filePath = directory.resolve(id + ".ser");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return deleteUser;
    }

    @Override
    public UserResDTO deleteUser(String userName) {
        UserResDTO deleteUser = getUser(userName);
        Path filePath = directory.resolve(deleteUser.getId() + ".ser");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return deleteUser;
    }
}