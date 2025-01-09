package com.sprint.mission.discodeit.common;

import java.util.regex.Pattern;

// TODO: libphonenumber 라이브러리 사용해보기!
public class Phone {
    private String phone;
    private String regionCode;

    public Phone(String phone, String regionCode){
        this.phone = phone;
        this.regionCode = regionCode;
    }

    public boolean validate(String phone, String regionCode){

        return true;
    }
}
