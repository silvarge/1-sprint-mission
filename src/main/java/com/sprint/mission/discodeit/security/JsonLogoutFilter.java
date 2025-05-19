package com.sprint.mission.discodeit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JsonLogoutFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if ("/api/auth/logout".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(
        request.getMethod())) {
      // 세션 무효화
      HttpSession session = request.getSession(false);
      if (session != null) {
        session.invalidate();
      }

      // Security Context 초기화
      SecurityContextHolder.clearContext();

      // 로그아웃 응답
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write("{\"message\" : \"logout success\"}");
      return;
    }

    filterChain.doFilter(request, response);

  }
}
