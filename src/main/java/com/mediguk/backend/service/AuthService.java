package com.mediguk.backend.service;

import com.mediguk.backend.dto.RequestOtpDTO;
import com.mediguk.backend.entity.Otp;
import com.mediguk.backend.repository.OtpRepository;
import com.mediguk.backend.repository.UserRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

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
}
