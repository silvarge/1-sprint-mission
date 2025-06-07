package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableCaching
public class CaffeineCacheConfig {

  @Bean
  public CacheManager caffeineCacheManager(Caffeine<Object, Object> spec) {
    CaffeineCacheManager manager = new CaffeineCacheManager("userChannels", "userNotifications",
        "allUsers");
    manager.setCaffeine(spec);
    return manager;
  }

  @Bean
  public Caffeine<Object, Object> caffeineSpec() {
    return Caffeine.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .expireAfterWrite(30, TimeUnit.MINUTES)
//        .refreshAfterWrite(5, TimeUnit.MINUTES)
        .maximumSize(5000)
        .recordStats()
        .removalListener((key, value, cause) -> {
          log.info("Cache Removed: key={}, cause={}", key, cause);
        });
  }
}
