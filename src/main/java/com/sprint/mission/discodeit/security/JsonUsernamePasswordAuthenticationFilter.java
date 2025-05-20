package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Slf4j
// json 형식의 로그인 요청 본문 파싱
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final ObjectMapper objectMapper;

  public JsonUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
      ObjectMapper objectMapper) {
    super.setAuthenticationManager(authenticationManager);
    this.objectMapper = objectMapper;
    setFilterProcessesUrl("/api/auth/login"); // 요청 처리할 URL 경로 설정

    // 응답 핸들러 설정 (성공/실패)
    setAuthenticationSuccessHandler((request, response, authentication) -> {
      HttpSession session = request.getSession(true);

      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authentication);
      session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
          context);

      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write("{\"message\": \"login success\"}");
    });

    setAuthenticationFailureHandler((request, response, exception) -> {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write("{\"message\": \"login failed\"}");
    });

  }

  // 인증 로직 수행
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    try {
      // 역직렬화 (JSON 본문)
      LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(),
          LoginRequest.class);
      // 인증 토큰 생성
      UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
          loginRequest.getUsername(), loginRequest.getPassword());

      // 인증 토큰에 요청 관련 정보 추가
      setDetails(request, authRequest);

      // 실제 인증 처리
      return this.getAuthenticationManager().authenticate(authRequest);
    } catch (IOException ioe) {
      // JSON 파싱 실패 시 예외 처리
      throw new RuntimeException("Failed to parse JSON login request", ioe);
    }
  }

  @Getter
  public static class LoginRequest {

    private String username;
    private String password;

  }
}
