package com.sprint.mission.discodeit.app;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class JavaApplication {
    public static void main(String[] args) {
        // User
        JCFUserService userService = new JCFUserService();
        // 1. 등록
        Long user1 = userService.createUserData(new UserReqDTO(
                "username1",
                "nickname1",
                "email@mail.com",
                "password!1234",
                "KR",
                "010-1111-2222",
                null
        ));
        Long user2 = userService.createUserData(new UserReqDTO("username2","nickname2","email2@mail.com","password!5678", "KR","010-2222-2222",null));
        Long user3 = userService.createUserData(new UserReqDTO("username3","nickname3","email3@mail.com","password!2345", "KR","010-3333-2222","test.png"));
        Long user4 = userService.createUserData(new UserReqDTO("username4","nickname4","email4@mail.com","password!3456", "KR","010-4444-2222","GOOd.png"));
        Long user5 = userService.createUserData(new UserReqDTO("username5","nickname5","email5@mail.com","password!0987", "KR","010-5555-2222","HAPPY.png"));

        System.out.println("------------------------------");

        // 2.1 조회(단건)
        UserResDTO findUser = userService.getUser(user1);
        System.out.println(String.format("ID(Index): %d, UUID: %s, UserName: %s, Nickname: %s, Email: %s",
                findUser.getId(), findUser.getUuid(), findUser.getUserName(), findUser.getNickname(), findUser.getEmail()));

        findUser = userService.getUser("username4");
        System.out.println(String.format("ID(Index): %d, UUID: %s, UserName: %s, Nickname: %s, Email: %s",
                findUser.getId(), findUser.getUuid(), findUser.getUserName(), findUser.getNickname(), findUser.getEmail()));

        System.out.println("------------------------------");

        // 2.2 조회(다건)
        List<UserResDTO> userList = userService.getAllUser();
        // TODO: 이건 이해해보고 넘어갑쉬당
        IntStream.range(0, userList.size()).forEach(i -> {
            UserResDTO user = userList.get(i);
            System.out.println(String.format("ID(Index): %d, UUID: %s, UserName: %s, Nickname: %s, Email: %s",
                    i, user.getUuid(), user.getUserName(), user.getNickname(), user.getEmail()));
        });

        System.out.println("------------------------------");

        // 3.1 수정
        long idx = 2;
        boolean updateFlag = userService.updateUser(idx,
                new UserUpdateDTO(null, "changeNickname", null, null,
                        null,null, "TTT.jpeg", null));
        if(updateFlag){
            // update가 되었다면
            UserResDTO user = userList.get((int)idx);
            System.out.println(String.format("[UPDATE LOG]\nID(Index): %d, UUID: %s, UserName: %s, Nickname: %s, Email: %s",
                    idx, user.getUuid(), user.getUserName(), user.getNickname(), user.getEmail()));
        }else{
            System.out.println("[UPDATE LOG]\n수정된 내용이 존재하지 않습니다.");
        }

        idx = 3;
        updateFlag = userService.updateUser(idx,
                new UserUpdateDTO(null, null, "email3@mail.com", null,
                        null,null, null, null));
        if(updateFlag){
            // update가 되었다면
            UserResDTO user = userList.get((int)idx);
            System.out.println(String.format("[UPDATE LOG]\nID(Index): %d, UUID: %s, UserName: %s, Nickname: %s, Email: %s",
                    idx, user.getUuid(), user.getUserName(), user.getNickname(), user.getEmail()));
        }else{
            System.out.println("[UPDATE LOG]\n수정된 내용이 존재하지 않습니다.");
        }

        System.out.println("------------------------------");

        // 4.1 삭제
        idx = 4;
        UserResDTO deletedUser = userService.deleteUser(idx);
        System.out.println(String.format("[DELETE LOG]\nID(Index): %d, UUID: %s, UserName: %s, Nickname: %s, Email: %s",
                idx, deletedUser.getUuid(), deletedUser.getUserName(), deletedUser.getNickname(), deletedUser.getEmail()));

        System.out.println("-----------[AFTER DELETE]----------");

        // 4.2 조회를 통해 삭제되었는지 확인
        List<UserResDTO> deleteUserList = userService.getAllUser();
        // TODO: 이건 이해해보고 넘어갑쉬당
        IntStream.range(0, deleteUserList.size()).forEach(i -> {
            UserResDTO user = deleteUserList.get(i);
            System.out.println(String.format("ID(Index): %d, UUID: %s, UserName: %s, Nickname: %s, Email: %s",
                    i, user.getUuid(), user.getUserName(), user.getNickname(), user.getEmail()));
        });

        System.out.println("------------------------------");

        // Channel
        JCFChannelService channelService = new JCFChannelService();

        // 1. 등록
        Optional<Map.Entry<Long, User>> owner = userService.findUserByUserName("username1");
        if(owner.isEmpty()) {
            System.out.println("사용자가 존재하지 않습니다.");
            return ;
        }

        Optional<Map.Entry<Long, User>> owner2 = userService.findUserByUserName("username3");
        if(owner2.isEmpty()) {
            System.out.println("사용자가 존재하지 않습니다.");
            return ;
        }

        Long ch1 = channelService.createChannel(
                new ChannelReqDTO(owner.get().getValue(), "server1", null, null)
        );
        Long ch2 = channelService.createChannel(new ChannelReqDTO(owner.get().getValue(), "server2", null, "ICON.jpg"));
        Long ch3 = channelService.createChannel(new ChannelReqDTO(owner2.get().getValue(), "server3", "TEST", null));
        Long ch4 = channelService.createChannel(new ChannelReqDTO(owner2.get().getValue(), "server9", "TTT", "ICONID.png"));
        Long ch5 = channelService.createChannel(new ChannelReqDTO(owner2.get().getValue(), "나는 서버다", "나는 서버다앙", "server.jpeg"));

        System.out.println("채널 생성 완료!");

        System.out.println("------------------------------");

        // 2.1 조회(단건)
        ChannelResDTO findChannel = channelService.getChannel(ch1);
        System.out.println(String.format("ID(Index): %d, UUID: %s, ChannelName: %s, OwnerName: %s, Description: %s, IconImgPath: %s",
                findChannel.getId(), findChannel.getUuid(), findChannel.getServerName(), findChannel.getOwnerName(), findChannel.getDescription(), findChannel.getIconImgPath()));

        findChannel = channelService.getChannel(ch5);
        System.out.println(String.format("ID(Index): %d, UUID: %s, ChannelName: %s, OwnerName: %s, Description: %s, IconImgPath: %s",
                findChannel.getId(), findChannel.getUuid(), findChannel.getServerName(), findChannel.getOwnerName(), findChannel.getDescription(), findChannel.getIconImgPath()));

        System.out.println("------------------------------");

        // 2.2 조회(다건)
        List<ChannelResDTO> channelList = channelService.getAllChannel();
        IntStream.range(0, channelList.size()).forEach(i -> {
            ChannelResDTO channel = channelList.get(i);
            System.out.println(String.format("ID(Index): %d, UUID: %s, ChannelName: %s, OwnerName: %s, Description: %s, IconImgPath: %s",
                    channel.getId(), channel.getUuid(), channel.getServerName(), channel.getOwnerName(), channel.getDescription(), channel.getIconImgPath()));
        });
        System.out.println("------------------------------");

        // 3.1 수정
        idx = 2;
        updateFlag = channelService.updateChannelInfo(idx,
                new ChannelUpdateDTO(null, "sssssssss서버", "HAPPY", null));
        if(updateFlag){
            // update가 되었다면
            ChannelResDTO channel = channelService.getChannel(idx);
            System.out.println(String.format("[UPDATE LOG]\nID(Index): %d, UUID: %s, ChannelName: %s, OwnerName: %s, Description: %s, IconImgPath: %s",
                    channel.getId(), channel.getUuid(), channel.getServerName(), channel.getOwnerName(), channel.getDescription(), channel.getIconImgPath()));
        }else{
            System.out.println("[UPDATE LOG]\n수정된 내용이 존재하지 않습니다.");
        }

        idx = 1;
        User changeOwner = userService.getUserToUserObj(2L);
        updateFlag = channelService.updateChannelInfo(idx,
                new ChannelUpdateDTO(changeOwner, null, "주인을 바꿨답니다~", null));
        if(updateFlag){
            // update가 되었다면
            ChannelResDTO channel = channelService.getChannel(idx);
            System.out.println(String.format("[UPDATE LOG]\nID(Index): %d, UUID: %s, ChannelName: %s, OwnerName: %s, Description: %s, IconImgPath: %s",
                    channel.getId(), channel.getUuid(), channel.getServerName(), channel.getOwnerName(), channel.getDescription(), channel.getIconImgPath()));
        }else{
            System.out.println("[UPDATE LOG]\n수정된 내용이 존재하지 않습니다.");
        }

        updateFlag = channelService.updateChannelInfo(4L,
                new ChannelUpdateDTO(null, null, null, null));
        if(updateFlag){
            // update가 되었다면
            ChannelResDTO channel = channelService.getChannel(idx);
            System.out.println(String.format("[UPDATE LOG]\nID(Index): %d, UUID: %s, ChannelName: %s, OwnerName: %s, Description: %s, IconImgPath: %s",
                    channel.getId(), channel.getUuid(), channel.getServerName(), channel.getOwnerName(), channel.getDescription(), channel.getIconImgPath()));
        }else{
            System.out.println("[UPDATE LOG]\n수정된 내용이 존재하지 않습니다.");
        }
        System.out.println("------------------------------");

        // 4.1 삭제
        idx = 4;
        ChannelResDTO channel = channelService.deleteChannel(idx);
        System.out.println(String.format("[DELETE LOG]\nID(Index): %d, UUID: %s, ChannelName: %s, OwnerName: %s, Description: %s, IconImgPath: %s",
                channel.getId(), channel.getUuid(), channel.getServerName(), channel.getOwnerName(), channel.getDescription(), channel.getIconImgPath()));

        System.out.println("-----------[AFTER DELETE]----------");

        // 4.2 조회를 통해 삭제되었는지 확인
        List<ChannelResDTO> deleteChannelList = channelService.getAllChannel();
        IntStream.range(0, deleteChannelList.size()).forEach(i -> {
            ChannelResDTO afterChannel = deleteChannelList.get(i);
            System.out.println(String.format("ID(Index): %d, UUID: %s, ChannelName: %s, OwnerName: %s, Description: %s, IconImgPath: %s",
                    afterChannel.getId(), afterChannel.getUuid(), afterChannel.getServerName(), afterChannel.getOwnerName(), afterChannel.getDescription(), afterChannel.getIconImgPath()));
        });

        System.out.println("------------------------------");


        // Message
        // 1. 등록

        // 2.1 조회(단건)

        // 2.2 조회(다건)

        // 3.1 수정

        // 3.2 수정된 데이터 조회

        // 4.1 삭제

        // 4.2 조회를 통해 삭제되었는지 확인


    }
}
