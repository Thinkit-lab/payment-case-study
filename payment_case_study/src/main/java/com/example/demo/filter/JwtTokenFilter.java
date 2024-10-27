package com.example.demo.filter;

import com.example.demo.auth.JwtService;
import com.example.demo.exceptions.AuthenticationException;
import com.example.demo.payload.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.example.demo.constants.ResponseStatus.AUTHENTICATION_ERROR;
import static com.example.demo.constants.ResponseStatus.FAILED;
import static com.example.demo.constants.StringValues.INVALID_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
  private static final List<String> ALLOWED_PATHS =
      List.of(
          "/v3/api-docs",
          "/swagger-ui",
          "/health",
              "/api/v1/auth");

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final ObjectMapper mapper;

  private static String getToken(HttpServletRequest request) {
    String header = request.getHeader(AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      throw new AuthenticationException("invalid bearer token supplied", AUTHENTICATION_ERROR.getCode());
    }
    return header.substring(7);
  }

  private static boolean isAllowedPath(HttpServletRequest request) {
    return ALLOWED_PATHS.stream().anyMatch(request.getRequestURI()::startsWith)
        || ALLOWED_PATHS.stream().anyMatch(request.getRequestURI()::contains);
  }

  @Override
  protected void doFilterInternal(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain)
      throws IOException {
    try {
      if (isAllowedPath(request)) {
        filterChain.doFilter(request, response);
        return;
      }

      String token = getToken(request);
      String userEmail = jwtService.extractUsername(token);

      if (userEmail == null || userEmail.isEmpty()) {
        log.info(INVALID_TOKEN);
        throw new AuthenticationException(INVALID_TOKEN, AUTHENTICATION_ERROR.getCode());
      }

      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
      boolean isTokenValid = jwtService.isTokenValid(token, userDetails);

      if (!isTokenValid) {
        throw new AuthenticationException("token validation failed", AUTHENTICATION_ERROR.getCode());
      }

      request.setAttribute("token", token);
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      log.error("token validation failed", e);
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter()
              .write(mapper.writeValueAsString(new BaseResponse<>(FAILED.getStatus(),
                      e.getMessage(), AUTHENTICATION_ERROR.getCode(), null)));
    }
  }
}
