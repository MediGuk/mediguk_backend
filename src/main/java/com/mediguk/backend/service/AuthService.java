package com.mediguk.backend.service;

import com.mediguk.backend.dto.RequestOtpDTO;
import com.mediguk.backend.dto.VerifyOtpDTO;
import com.mediguk.backend.entity.AuthSession;
import com.mediguk.backend.entity.Otp;
import com.mediguk.backend.model.AuthResult;
import com.mediguk.backend.model.SessionCreationResult;
import com.mediguk.backend.repository.OtpRepository;
import com.mediguk.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @RequiredArgsConstructor to inject automatically ?? worth it ???
@Service // Spring automatically: new AuthService(userRepositoryBean, otpRepositoryBean)  //
// Dependency injection
public class AuthService {

  // Repositories injected by Spring (Dependency Injection)
  private final UserRepository userRepository;
  private final OtpRepository otpRepository;
  private final PasswordEncoder passwordEncoder;
  private final SessionService sessionService;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository,
      OtpRepository otpRepository,
      PasswordEncoder passwordEncoder,
      SessionService sessionService,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.otpRepository = otpRepository;
    this.passwordEncoder = passwordEncoder;
    this.sessionService = sessionService;
    this.jwtService = jwtService;
  }

  @Transactional // Method as only one operaton in DB (others wait behind)(protects against 2
  // request at same time)
  public void requestOtp(RequestOtpDTO dto) {

    // 1. Extract data from the incoming request DTO
    String documentNumber = dto.getDocumentNumber();

    // Get actual time
    LocalDateTime now = LocalDateTime.now();

    // 2. Find user in database by DNI/NIE (FUTURE: database of osakidetza)
    var user =
        userRepository
            .findByDocumentNumber(documentNumber)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 3 Cheack last OTP of user
    var lastOtp = otpRepository.findTopByUserOrderByExpiresAtDesc(user);

    // 3.1 If OTP is present in user
    if (lastOtp.isPresent()) {

      // get the otp
      Otp otp = lastOtp.get();

      // if OTP not used & valid → reuse it
      if (!otp.isUsed() && otp.getExpiresAt().isAfter(now)) {

        System.out.println("Reusing existing OTP: " + otp.getCode());

        // has to pass 60 seconds from last sent the OTP
        if (otp.getLastSentAt().plusSeconds(60).isAfter(now)) {
          throw new RuntimeException("Please wait before requesting another OTP");
        }

        otp.setLastSentAt(now);

        // FUTURE: Send OTP via SMS/Whatsapp/email
        return;
      }
    }

    // ***** 4. If NO last validated OTP, generate a secure OTP *****//
    // 4.1 Invalidate all before otps to used= true
    otpRepository.invalidateOtpsForUser(user);

    // 4.2 Generate secure OTP
    // Math.random() is not safe for security-related operations /// 6-digit OTP using SecureRandom
    SecureRandom random = new SecureRandom();
    int otpCode = random.nextInt(900000) + 100000;
    System.out.println("OTP generated: " + otpCode);

    // 4.3 Hash the OTP. FUTURE: hash with SHA
    String otpPlain = String.valueOf(otpCode);
    String otpHashed = passwordEncoder.encode(otpPlain);

    // 4. Create a new OTP entity
    Otp otp = new Otp();

    // 5. Set OTP properties
    otp.setCode(otpHashed);
    otp.setCreatedAt(now);
    otp.setLastSentAt(now);
    otp.setExpiresAt(now.plusMinutes(5));
    otp.setUser(user); // JOIN user

    otp.setAttempts(0);
    otp.setUsed(false);

    // 6. Save OTP in the database
    otpRepository.save(otp);
  }

  @Transactional
  public AuthResult verifyOtp(
      VerifyOtpDTO dto, HttpServletRequest request) { // ip, userAgent, deviceId

    // 1. Extract data from the incoming request DTO
    String documentNumber = dto.getDocumentNumber();
    String otpRequest = dto.getOtp();

    LocalDateTime now = LocalDateTime.now();

    // 2. Find user in database by DNI/NIE
    var user =
        userRepository
            .findByDocumentNumber(documentNumber)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 3. Find the most recent OTP of the user (string)
    // @Transactional: Charged the entity - persistent context
    var otp =
        otpRepository
            .findActiveOtp(user, now)
            .orElseThrow(() -> new RuntimeException("OTP not found"));

    String otpStored = otp.getCode();

    // 4.OTP securities
    // 4.0 Check if OTP is used // FUTURE: delete cos in query i do or need specific error for UX?
    if (otp.isUsed()) {
      throw new RuntimeException("OTP already used");
    }

    // 4.1 User made too many attempts
    LocalDateTime since = now.minusMinutes(10);

    Integer attempts = otpRepository.countRecentAttempts(user, since);

    if (attempts != null && attempts >= 10) {
      throw new RuntimeException("Too many verification attempts. Try later.");
    }

    // 4.2 Check max attempts of token
    if (otp.getAttempts() >= 5) {
      throw new RuntimeException("Too many OTP attempts");
    }

    // 5. Compare OTPhash saved VS OTPstring sent
    if (!passwordEncoder.matches(otpRequest, otpStored)) {
      otp.setAttempts(otp.getAttempts() + 1);
      throw new RuntimeException("Invalid OTP");
    }

    // @Scheduled cron of spring, each hour deletes the expired OTPs

    otp.setUsed(true); // PERSISTENT-CONTEXT (no need of save)
    // otpRepository.save(otp);

    // 6. Generate session: authSession, sessionToken, fingerprintHash
    SessionCreationResult sessionResult = sessionService.createSession(user, request);

    AuthSession session = sessionResult.session();
    String fingerprint = sessionResult.fingerprint();

    // 7. Generate token JWT
    String accessToken = jwtService.generateToken(user.getId(), session.getSessionToken());

    // 8. Return access token(JWT) & fingerprint
    return new AuthResult(accessToken, fingerprint);
  }
}
