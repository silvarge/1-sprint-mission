package com.sprint.mission.discodeit.common;

import com.sprint.mission.discodeit.enums.RegionCode;

import java.util.regex.Pattern;

// TODO: libphonenumber 라이브러리 사용해보기!
public class Phone {
    private String phone;
    private RegionCode regionCode;

    public Phone(String phone, RegionCode regionCode){
        if(validate(phone)){
            this.phone = phone;
            this.regionCode = regionCode;
        }else{
            throw new IllegalArgumentException("잘못된 Phone 형식입니다.");
        }
    }

    public String getPhone() {
        return phone;
    }

    public RegionCode getRegionCode() {
        return regionCode;
    }

    public boolean validate(String phone){
        // TODO: 일단 번호만
        String regex ="^\\d{2,3}-\\d{3,4}-\\d{4}$";
        return Pattern.matches(regex, phone);
    }
}
