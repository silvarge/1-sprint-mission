package com.sprint.mission.discodeit.common;

import com.fasterxml.jackson.annotation.JsonValue;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Phone {
    @JsonValue
    @Column(name = "phone_num", length = 20, nullable = false)
    private String phone;

    @Column(name = "phone_region", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private RegionCode regionCode;

    @Override
    public String toString() {
        return phone;
    }

    @AllArgsConstructor
    public enum RegionCode {
        KR("82"),
        US("1"),
        ;

        private final String regionNum;

        public static RegionCode fromString(String value) {
            if (StringUtils.isBlank(value)) {
                throw new IllegalArgumentException("지역 코드는 null이 될 수 없습니다.");
            }
            try {
                return RegionCode.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("잘못된 지역 코드: " + value);
            }
        }
    }
}
