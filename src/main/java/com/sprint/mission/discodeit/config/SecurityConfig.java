package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.JsonLogoutFilter;
import com.sprint.mission.discodeit.security.JsonUsernamePasswordAuthenticationFilter;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserDetailsService userDetailsService;

  @Value("${discodeit.cookie.persistent-key}")
  private String cookiePersistentKey;

  @Bean
  public PersistentTokenRepository tokenRepository(DataSource dataSource) {
    JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
    repository.setDataSource(dataSource);

    repository.setCreateTableOnStartup(false);

    return repository;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http,
      AuthenticationManager authenticationManager, PersistentTokenRepository tokenRepository,
      RememberMeServices rememberMeServices)
      throws Exception {

    CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
    repo.setCookiePath("/");
    repo.setHeaderName("X-CSRF-TOKEN");  // 프론트에서 이 이름으로 헤더 보냄
    repo.setCookieName("CSRF-TOKEN");
    repo.setCookieCustomizer(builder -> builder.sameSite("None").secure(true));

    XorCsrfTokenRequestAttributeHandler handler = new XorCsrfTokenRequestAttributeHandler();
    handler.setCsrfRequestAttributeName("_csrf");

    http
        // CSRF 설정: 쿠키 연동 시 CookiesCsrfTokenRepository 사용
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/api/users")
            .csrfTokenRepository(repo)
            .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()::handle)
        )
        // 인증 정보를 서버 세션에 저장
        .securityContext(context -> context
            .securityContextRepository(new HttpSessionSecurityContextRepository()))
        // 세션 정책: 인증 성공 시 세션 생성
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        )
        // URL 별 인증 규칙 설정
        .authorizeHttpRequests(auth -> auth
            // CSRF 토큰 발급 API는 인증하지 않음 /
            .requestMatchers("/api/auth/csrf-token", "/api/users", "/api/auth/login",
                "/api/auth/logout").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/channels/public").hasRole("CHANNEL_MANAGER")
            .requestMatchers(HttpMethod.PUT, "/api/channels/**").hasRole("CHANNEL_MANAGER")
            .requestMatchers(HttpMethod.DELETE, "/api/channels/**").hasRole("CHANNEL_MANAGER")
            .requestMatchers("/api/**").hasRole("USER")

            .anyRequest().authenticated()
        )
        // 인증 공급자 등록 (UserDetailsService + PasswordEncoder 기반)
        .authenticationProvider(authenticationProvider())
        // 커스텀 로그인 필터 등록
        .addFilterBefore(
            usernamePasswordAuthenticationFilter(authenticationManager, rememberMeServices),
            UsernamePasswordAuthenticationFilter.class)
        // 기본 세팅 해제
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .httpBasic(Customizer.withDefaults())
        .rememberMe(r -> r
            .rememberMeCookieName("PERSIST")
            .rememberMeParameter("remember")
            .tokenRepository(tokenRepository)
            .tokenValiditySeconds(60 * 60 * 24 * 21)
            .key(cookiePersistentKey)
        );
    return http.build();
  }

  // 비밀번호 암호화
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // 비밀번호 암호화 시 Bcrypt 해시 사용
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

  // 사용자 인증 수행
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService); // 사용자 정보 로딩
    provider.setPasswordEncoder(passwordEncoder());     // 비밀번호 인코딩 전략 (비밀번호 암호화)
    return provider;
  }

  // 로그인 필터 등록
  @Bean
  public JsonUsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter(
      AuthenticationManager authenticationManager, RememberMeServices rememberMeServices) {
    return new JsonUsernamePasswordAuthenticationFilter(authenticationManager,
        rememberMeServices);
  }

  // 로그아웃 필터 등록
  @Bean
  public JsonLogoutFilter jsonLogoutFilter(PersistentTokenRepository tokenRepository) {
    return new JsonLogoutFilter(tokenRepository);
  }

  // 권한에 계층 부여
  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role("ADMIN").implies("CHANNEL_MANAGER")
        .role("CHANNEL_MANAGER").implies("USER")
        .build();
  }

  @Bean
  public GrantedAuthoritiesMapper authoritiesMapper(RoleHierarchy roleHierarchy) {
    return new RoleHierarchyAuthoritiesMapper(roleHierarchy);
  }

  // RememberME
  @Bean
  public RememberMeServices rememberMeServices(PersistentTokenRepository tokenRepository) {
    return new PersistentTokenBasedRememberMeServices(
        cookiePersistentKey,
        userDetailsService,
        tokenRepository
    );
  }


}
