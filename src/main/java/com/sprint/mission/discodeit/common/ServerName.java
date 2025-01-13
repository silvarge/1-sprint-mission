package com.sprint.mission.discodeit.common;

import java.util.regex.Pattern;

public class ServerName implements Name {
    private String name;

    public ServerName(String name){
        if(validate(name)) {
            this.name = name;
        }else{
            throw new IllegalArgumentException("잘못된 Server Name 형식입니다.");
        }
    }

    public String getName() {
        return name;
    }

    public boolean setServerName(String serverName){
        if(validate(serverName)){
            this.name = serverName;
            return true;
        }else{
            return false;
        }
    }


    @Override
    public boolean validate(String name) {
        // 지금 봤을 때는 크게 제한이 없어 보임
        // but 추후 필터링이 필요할 수 있다고 판단
        return true;
    }
}
