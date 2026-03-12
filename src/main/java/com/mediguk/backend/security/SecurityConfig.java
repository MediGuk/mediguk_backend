package com.mediguk.backend.security;

import com.mediguk.backend.repository.AuthSessionRepository;
import com.mediguk.backend.service.JwtService;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity // Activate spring security
public class SecurityConfig {

  private final JwtService jwtService;
  private final AuthSessionRepository authSessionRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.cors.allowed-origin}")
  private String allowedOrigin;

  public SecurityConfig(
      JwtService jwtService,
      AuthSessionRepository authSessionRepository,
      PasswordEncoder passwordEncoder) {
    this.jwtService = jwtService;
    this.authSessionRepository = authSessionRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtService, authSessionRepository, passwordEncoder);
  }

  // PROTECT THE ENDPOINT
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.cors(cors -> {}) // activate CORS filters of Spring
        .csrf(csrf -> csrf.disable()) // cos we have JWT & stateless API
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // Define public endpoint (no need to be autenticated with JWT or fingerprint)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/auth/**", "/otp/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())

        // Authenticate de user with fileter before even springSecurity decides tu access or not
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  // CORS configuration for Spring
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(List.of(allowedOrigin));

    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    config.setAllowedHeaders(List.of("*")); // Authorization, Content-Type, Cookie ....

    config.setAllowCredentials(
        true); // IMPORTANT: Client in header need to put credentials: "include"

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config); // apply this rules to all the API

    return source;
  }
}
