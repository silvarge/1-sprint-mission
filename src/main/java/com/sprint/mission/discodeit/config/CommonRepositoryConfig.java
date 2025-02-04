package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class CommonRepositoryConfig {

    @Bean
    public Path fileUserStoragePath() {
        return Paths.get(System.getProperty("user.dir"), "/temp/user");
    }

    @Bean
    public Path fileChannelStoragePath() {
        return Paths.get(System.getProperty("user.dir"), "/temp/channel");
    }

    @Bean
    public Path fileMessageStoragePath() {
        return Paths.get(System.getProperty("user.dir"), "/temp/message");
    }
}
