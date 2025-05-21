package com.sprint.mission.discodeit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JsonLogoutFilter extends OncePerRequestFilter {

  private final PersistentTokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if ("/api/auth/logout".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(
        request.getMethod())) {

      // 토큰 삭제
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null && auth.isAuthenticated()) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        tokenRepository.removeUserTokens(userDetails.getUsername());
      }

      // 세션 무효화
      HttpSession session = request.getSession(false);
      if (session != null) {
        session.invalidate();
      }

      // Security Context 초기화
      SecurityContextHolder.clearContext();

      //
      ResponseCookie jsessionCookie = ResponseCookie.from("JSESSIONID", "")
          .maxAge(0)               // 즉시 만료
          .path("/")               // 반드시 경로 지정
          .httpOnly(true)
          .secure(false)    // todo: https가 아닌 경우 쿠키 설정 무시 -> https로 안되니까 일단 이렇게 해 둠
          .build();
      response.addHeader(HttpHeaders.SET_COOKIE, jsessionCookie.toString());

      ResponseCookie csrfCookie = ResponseCookie.from("CSRF-TOKEN", "")
          .path("/")
          .maxAge(0)
          .httpOnly(false)  // JS에서 읽기 가능하게
          .secure(false)    // 로컬 환경이라면 false
          .build();
      response.addHeader(HttpHeaders.SET_COOKIE, csrfCookie.toString());

      // 로그아웃 응답
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      return;
    }

    filterChain.doFilter(request, response);

  }
}
