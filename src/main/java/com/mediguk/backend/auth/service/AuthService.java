package com.mediguk.backend.auth.service;

import com.mediguk.backend.auth.dto.RequestOtpDTO;
import com.mediguk.backend.auth.dto.VerifyOtpDTO;
import com.mediguk.backend.auth.entity.AuthSession;
import com.mediguk.backend.auth.entity.User;
import com.mediguk.backend.auth.model.AuthResult;
import com.mediguk.backend.auth.model.SessionCreationResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // Spring automatically: new AuthService(userRepositoryBean, otpRepositoryBean)  //
@RequiredArgsConstructor // Dependency injection automatically. NO NEED of manualconstructor args
// with this.
public class AuthService {

  // Repositories injected by Spring (Dependency Injection)
  private final SessionService sessionService;
  private final JwtService jwtService;
  private final UserService userService;
  private final OtpService otpService;

  @Transactional // One operaton at the same time in DB (protects against 2 request at same time)
  public void requestLogin(RequestOtpDTO dto) {

    // 1. Get user by documentData from incoming request
    User user = userService.getUserByDocument(dto.getDocumentNumber());

    // 2. Create and send OTP attached to the user
    otpService.requestOtp(user);
  }

  @Transactional
  public AuthResult verifyLogin(
      VerifyOtpDTO dto, HttpServletRequest request) { // ip, userAgent, deviceId

    // 1. Extract data from the incoming request DTO
    String documentNumber = dto.getDocumentNumber();
    String otpRequest = dto.getOtp();

    // 2. Obtain user
    User user = userService.getUserByDocument(documentNumber);

    // 3. Validate OTP
    otpService.validateOtp(user, otpRequest);

    // 4. Generate session: authSession, sessionToken, fingerprintHash
    SessionCreationResult sessionResult = sessionService.createSession(user, request);

    AuthSession session = sessionResult.session();
    String fingerprintRaw = sessionResult.fingerprint();
    String refreshToken = session.getRefreshToken();

    // 5. Generate token JWT
    String jwtToken = jwtService.generateToken(user.getId(), session.getSessionToken());

    // 6. Return access token(JWT) & fingerprint
    return new AuthResult(jwtToken, refreshToken, fingerprintRaw);
  }

  @Transactional
  public void logout(String accessToken) {
    // 1. Take sessionToken from JWT
    String sessionToken = jwtService.parseToken(accessToken).get("sessionToken", String.class);

    // 2. Revoke session on DB to fail in filter
    sessionService.revokeSession(sessionToken);

    System.out.println("Sesión revocada: " + sessionToken);
  }

  @Transactional
  public AuthResult refresh(String actualRefreshToken, String fingerprintRaw) {

    // 1. Give a session which is validated by refreshToken, expired/revoked FALSE, fingerprint
    AuthSession session = sessionService.getValidSession(actualRefreshToken, fingerprintRaw);

    // 2. Rotate Refresh Token
    String newRefreshToken = sessionService.rotateRefreshToken(session);

    // 3. Generate new JWT (old one is expired)
    String newJwt = jwtService.generateToken(session.getUser().getId(), session.getSessionToken());

    // 4. Return with new Jwt & refreshToken
    return new AuthResult(newJwt, newRefreshToken, fingerprintRaw);
  }
}
