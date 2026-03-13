package com.mediguk.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mediguk.backend.entity.Otp;
import com.mediguk.backend.entity.User;
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
}
