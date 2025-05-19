package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  // JSON 요청 파싱용
  private final ObjectMapper objectMapper;

  @Bean
  SecurityFilterChain chain(HttpSecurity http,
      JsonUsernamePasswordAuthenticationFilter customLoginFilter)
      throws Exception {
    http
        .csrf(csrf -> csrf
            // 회원가입 API는 CSRF 검사 안함
            .ignoringRequestMatchers("/api/users", "/api/auth/login")
            // 쿠키에 CSRF 토큰 저장 - JS에서 쿠키를 읽을 수 있게 함 (withHttpOnlyFalse)
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        )
        .authorizeHttpRequests(auth -> auth
            // CSRF 토큰 발급 API는 예외 (인증 수행 X)
            .requestMatchers("/api/auth/csrf-token", "/api/users", "/api/auth/login").permitAll()
            // /api/** 요청만 인증 요구
            .requestMatchers("/api/**").authenticated()
            // 그 외 요청은 인증 수행 X
            .anyRequest().permitAll())
        // UsernamePasswordAuthenticationFilter 위치에 커스텀 로그인 필터 등록
        .addFilterAt(customLoginFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }

  // 비밀번호 암호화
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // 사용자 인증 수행
  @Bean
  public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  // 인증 로직 관리
  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity httpSecurity,
      AuthenticationProvider provider)
      throws Exception {
    return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
        .authenticationProvider(provider)
        .build();
  }

  // 로그인 필터 등록
  @Bean
  public JsonUsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter(
      AuthenticationManager authenticationManager) {
    return new JsonUsernamePasswordAuthenticationFilter(authenticationManager, objectMapper);
  }

}
