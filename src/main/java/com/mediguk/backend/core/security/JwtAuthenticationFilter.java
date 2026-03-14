package com.mediguk.backend.core.security;

import com.mediguk.backend.auth.repository.AuthSessionRepository;
import com.mediguk.backend.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final AuthSessionRepository authSessionRepository;
  private final PasswordEncoder passwordEncoder;

  // constructor
  public JwtAuthenticationFilter(
      JwtService jwtService,
      AuthSessionRepository authSessionRepository,
      PasswordEncoder passwordEncoder) {
    this.jwtService = jwtService;
    this.authSessionRepository = authSessionRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 1. Get JWT from header -> authorization: Bearer <JWT>
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // Get JWT token removing bearer (7 char)
    String token = authHeader.substring(7);

    // Get all the attributes parsed from the claims/payload
    Claims claims = jwtService.parseToken(token);

    // Get userId and ssessionToken from JWT claims/payload
    String userId = claims.getSubject();
    String sessionToken = claims.get("sessionToken", String.class);

    // 2. Find and verify the sessionToken of the user in DB
    var session =
        authSessionRepository.findBySessionTokenAndRevokedFalse(sessionToken).orElse(null);

    if (session == null) {
      filterChain.doFilter(request, response);
      return;
    }

    // 3. Get fingerprint cookie from header
    Cookie[] cookies = request.getCookies();

    String fingerprint = null;

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("fingerprint".equals(cookie.getName())) {
          fingerprint = cookie.getValue();
          break;
        }
      }
    }

    if (fingerprint == null) {
      filterChain.doFilter(request, response);
      return;
    }

    // 4. Validate the fingerprint cookie with the server hash one
    boolean validFingerprint = passwordEncoder.matches(fingerprint, session.getFingerprintHash());

    if (!validFingerprint) {
      filterChain.doFilter(request, response);
      return;
    }

    // 5. User object autenthicated with Sring Security
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userId, // user authenticated
              null, // credentials (password)
              Collections.emptyList() // roles
              );

      // Saved authenticated user in SecurityContext
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 6. Pass the filter
    filterChain.doFilter(request, response);
  }
}
