package com.mediguk.backend.auth.service.impl;

import com.mediguk.backend.auth.entity.Otp;
import com.mediguk.backend.auth.entity.User;
import com.mediguk.backend.auth.repository.OtpRepository;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import com.mediguk.backend.auth.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor //Crea constructor auto
public class OtpServiceImpl implements OtpService {

  private final OtpRepository otpRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public String requestOtp(User user) {
    LocalDateTime now = LocalDateTime.now();

    // 1. Cheack last OTP of user to check if 60 secons passed already from last
    // sent
    Optional<Otp> lastOtpEntity = otpRepository.findTopByUserOrderByExpiresAtDesc(user); // can be null

    lastOtpEntity.ifPresent(
        lastOtp -> {
          // If OTP not used & not expired: Has to pass 60 seconds from last sent OTP
          // (avoid spam)
          if (!lastOtp.isUsed()
              && lastOtp.getExpiresAt().isAfter(now)
              && lastOtp.getLastSentAt().plusSeconds(60).isAfter(now)) {
            long timeLeft = Duration.between(now, lastOtp.getLastSentAt().plusSeconds(60)).toSeconds();
            throw new RuntimeException(
                "Please wait " + timeLeft + "s before requesting another OTP");
          }
        });

    // 2. If 60s passed already from last sent, each new request create new OTP
    otpRepository.invalidateOtpsForUser(user);

    // Generate 4 digits secure OTP
    SecureRandom random = new SecureRandom();
    int otpCode = random.nextInt(9000) + 1000;
    String otpPlain = String.valueOf(otpCode);

    // Crear entidad y guardar hasheado
    Otp newOtp = new Otp();
    newOtp.setUser(user);
    newOtp.setCode(passwordEncoder.encode(otpPlain));
    newOtp.setCreatedAt(now);
    newOtp.setLastSentAt(now);
    newOtp.setExpiresAt(now.plusMinutes(5));
    newOtp.setAttempts(0);
    newOtp.setUsed(false);

    otpRepository.save(newOtp);

    System.out.println("🆕 [OTP_SERVICE] Nuevo OTP generado para el usuario [" + user.getId() + "]:");
    System.out.println("   - Creado en: " + newOtp.getCreatedAt());
    System.out.println("   - Expira en: " + newOtp.getExpiresAt());
    System.out.println("   - Estado (used): " + newOtp.isUsed());

    return otpPlain;
    // FUTURE: Send otpPlain via WhatsApp/SMS/email
  }

  @Transactional
  public void validateOtp(User user, String plainOtp) {

    LocalDateTime now = LocalDateTime.now();

    System.out.println("🔍 [OTP_SERVICE] Validando OTP para usuario [" + user.getId() + "]...");
    System.out.println("   - Hora actual (System now): " + now);

    // Diagnóstico extra: Ver si existe algún OTP (aunque esté expirado o usado)
    Optional<Otp> latest = otpRepository.findTopByUserOrderByExpiresAtDesc(user);
    if (latest.isPresent()) {
      Otp o = latest.get();
      System.out.println("   - OTP más reciente en DB:");
      System.out.println("     * ID: " + o.getId());
      System.out.println("     * Expira en: " + o.getExpiresAt());
      System.out.println("     * Usado: " + o.isUsed());
      System.out.println("     * ¿Expirado respecto a 'now'?: " + o.getExpiresAt().isBefore(now));
    } else {
      System.out.println("   - [!] No se ha encontrado NINGÚN OTP para este usuario en la base de datos.");
    }

    // 1. Find the last NO expired/used OTP
    Otp lastOtpEntity = otpRepository
        .findActiveOtp(user, now)
        .orElseThrow(() -> new RuntimeException("OTP not found or expired"));

    // 2. Control of total attemps from all the OTPs in total made by the user
    Integer recentAttempts = otpRepository.countRecentAttempts(user, now.minusMinutes(10));
    if (recentAttempts != null && recentAttempts >= 10) {
      throw new RuntimeException("Too many verification attempts. Try later.");
    }

    // 3. Control max attempts of the last specific OTP
    if (lastOtpEntity.getAttempts() >= 5) {
      lastOtpEntity.setUsed(true); // Block OTP for security
      throw new RuntimeException("Too many OTP attempts for this code.");
    }

    // 4. 5. Compare OTPhash db VS OTPplain client
    if (!passwordEncoder.matches(plainOtp, lastOtpEntity.getCode())) {
      lastOtpEntity.setAttempts(lastOtpEntity.getAttempts() + 1);
      // Hibernate lo guarda solo por estar en @Transactional
      throw new RuntimeException("Invalid OTP");
    }

    // @Scheduled cron of spring, each hour deletes the expired OTPs

    // 5. Todo OK: Marcar como usado
    lastOtpEntity.setUsed(true); // PERSISTENT-CONTEXT (no need of save)
    // otpRepository.save(otp);
  }
}
