package com.sprint.mission.discodeit.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

// TODO: 이렇게 등록해야 되네.....
// 안그러면 계속 아래같은 오류나묘

/**
 * Failed to register application as Application(name=spring-boot-application, managementUrl=http://localhost:8080/actuator, healthUrl=http://localhost:8080/actuator/health, serviceUrl=http://localhost:8080) at spring-boot-admin ([localhost:9090/instances]): invalid URI scheme localhost. Further attempts are logged on DEBUG level
 * java.lang.IllegalArgumentException: invalid URI scheme localhost
 */
@Component
public class AdminManualRegistrar {

    @PostConstruct
    public void manualRegister() {
        Map<String, Object> body = Map.of(
                "name", "discodeit",
                "serviceUrl", "http://localhost:8080",
                "managementUrl", "http://localhost:8080/actuator",
                "healthUrl", "http://localhost:8080/actuator/health"
        );

        new RestTemplate().postForObject("http://localhost:9090/instances", body, String.class);
    }
}
