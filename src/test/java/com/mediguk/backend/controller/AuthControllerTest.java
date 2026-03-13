package com.mediguk.backend.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mediguk.backend.entity.AuthSession;
import com.mediguk.backend.entity.Otp;
import com.mediguk.backend.entity.User;
import com.mediguk.backend.repository.AuthSessionRepository;
import com.mediguk.backend.repository.OtpRepository;
import com.mediguk.backend.repository.UserRepository;
import com.mediguk.backend.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
      "spring.datasource.driverClassName=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      // valor cualquiera para el test de los .env
      "JWT_SECRET=estaesunaclavesecretadePruebas12345678901234567890",
      "FRONTEND_URL=http://localhost:3000"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test") // Importante: Lee application-test.properties con H2
@Transactional // Revierte los cambios en la DB tras cada test
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private OtpRepository otpRepository;
  @Autowired private AuthSessionRepository sessionRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @Test
  void verifyOtp_shouldReturnJwtAndCookies_whenDataIsValid() throws Exception {
    // 1. Setup: Save real user in H2 database
    User user = TestDataFactory.createUser("12345678Z");
    userRepository.save(user);

    // 2. Setup: Save OTP (hasheD) at H2
    String rawOtp = "123456";
    Otp otp = TestDataFactory.createOtp(user, passwordEncoder.encode(rawOtp));
    otpRepository.save(otp);

    // 3. Request JSON
    String jsonRequest = "{\"documentNumber\":\"12345678Z\", \"otp\":\"123456\"}";

    // 4. Test ejecución y verificación
    mockMvc
        .perform(
            post("/auth/verify-otp").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.jwtToken").exists())
        .andExpect(header().exists(HttpHeaders.SET_COOKIE)); // Verifica que llegan las cookies
  }

  @Test
  void requestOtp_shouldReturnOk_whenUserExists() throws Exception {
    // Setup
    User user = TestDataFactory.createUser("12345678Z");
    userRepository.save(user);

    String jsonRequest = "{\"documentNumber\":\"12345678Z\"}";

    // Ejecución: Pedir el OTP
    mockMvc
        .perform(
            post("/auth/request-otp").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andExpect(status().isOk());

    // Verificación: ¿Se guardó en la DB?
    boolean exists = otpRepository.findTopByUserOrderByExpiresAtDesc(user).isPresent();
    assert exists;
  }

  @Test
  void verifyOtp_shouldReturnFullAuthResult_whenOtpIsCorrect() throws Exception {
    // Setup
    User user = TestDataFactory.createUser("12345678Z");
    userRepository.save(user);

    String rawOtp = "123456";
    Otp otp = TestDataFactory.createOtp(user, passwordEncoder.encode(rawOtp));
    otpRepository.save(otp);

    String jsonRequest = "{\"documentNumber\":\"12345678Z\", \"otp\":\"123456\"}";

    // Ejecución
    mockMvc
        .perform(
            post("/auth/verify-otp").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.jwtToken").exists())
        // IMPORTANTE: Quitamos el check de $.refreshToken porque ahora es una COOKIE
        .andExpect(header().exists(HttpHeaders.SET_COOKIE))
        .andExpect(cookie().exists("refreshToken"))
        .andExpect(cookie().exists("fingerprint"));
  }

  @Test
  void refresh_shouldReturnNewTokens_whenSessionIsValid() throws Exception {
    // 1. Setup
    User user = TestDataFactory.createUser("12345678Z");
    userRepository.save(user);

    String rawFingerprint = "secret-fingerprint-123";
    String refreshToken = "valid-uuid-token";

    AuthSession session =
        TestDataFactory.createSession(
            user, refreshToken, passwordEncoder.encode(rawFingerprint) // Guardado hasheado
            );
    sessionRepository.save(session);

    jakarta.servlet.http.Cookie rtCookie =
        new jakarta.servlet.http.Cookie("refreshToken", refreshToken);
    jakarta.servlet.http.Cookie fpCookie =
        new jakarta.servlet.http.Cookie("fingerprint", rawFingerprint);

    // 3. NO body, just headers
    mockMvc
        .perform(
            post("/auth/refresh")
                .cookie(rtCookie)
                .cookie(fpCookie)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.jwtToken").exists())
        .andExpect(cookie().exists("refreshToken"));
  }
}
