package com.mediguk.backend.service;

import com.mediguk.backend.dto.RequestOtpDTO;
import com.mediguk.backend.dto.VerifyOtpDTO;
import com.mediguk.backend.entity.Otp;
import com.mediguk.backend.repository.OtpRepository;
import com.mediguk.backend.repository.UserRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // Spring automatically: new AuthService(userRepositoryBean, otpRepositoryBean)  //
// Dependency injection
public class AuthService {

  // Repositories injected by Spring (Dependency Injection)
  private final UserRepository userRepository;
  private final OtpRepository otpRepository;

  public AuthService(UserRepository userRepository, OtpRepository otpRepository) {
    this.userRepository = userRepository;
    this.otpRepository = otpRepository;
  }

  public void requestOtp(RequestOtpDTO dto) {

    // 1. Extract data from the incoming request DTO
    String documentNumber = dto.getDocumentNumber();

    // 2. Find user in database by DNI/NIE
    var user =
        userRepository
            .findByDocumentNumber(documentNumber)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 3. Generate a secure 6-digit OTP using SecureRandom
    // Math.random() is not safe for security-related operations
    SecureRandom random = new SecureRandom();
    int otpCode = random.nextInt(900000) + 100000;
    System.out.println("OTP generated: " + otpCode);

    // 4. Create a new OTP entity
    Otp otp = new Otp();

    // 5. Set OTP properties
    otp.setCode(String.valueOf(otpCode));
    otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
    otp.setUser(user); // JOIN user

    // 6. Save OTP in the database
    otpRepository.save(otp);
  }

  @Transactional
  public void verifyOtp(VerifyOtpDTO dto) {

    // 1. Extract data from the incoming request DTO
    String documentNumber = dto.getDocumentNumber();
    String otpRequest = dto.getOtp();

    // 2. Find user in database by DNI/NIE
    var user =
        userRepository
            .findByDocumentNumber(documentNumber)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 3. Find the most recent OTP of the user (string)
    // @Transactional: Charged the entity - persistent context
    var otp =
        otpRepository
            .findTopByUserOrderByExpiresAtDesc(user)
            .orElseThrow(() -> new RuntimeException("OTP not found"));

    String otpStored = otp.getCode();

    // 4. Check if OTP is used
    if (otp.isUsed()) {
      throw new RuntimeException("OTP already used");
    }

    // 5. Compare OTP saved VS OTP sent
    if (!otpStored.equals(otpRequest)) {
      throw new RuntimeException("Invalid OTP");
    }

    // 6. Check if OTP is expired //FUTURE: Instant OR OffsetDateTime to assure timezones UTC
    if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new RuntimeException("OTP expired");
    }

    otp.setUsed(true); // PERSISTENT-CONTEXT (no need of save)
    // otpRepository.save(otp);

  }
}
