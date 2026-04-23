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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor //Crea constructor auto
public class OtpServiceImpl implements OtpService {

  private final OtpRepository otpRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public String requestOtp(User user) {
    LocalDateTime now = LocalDateTime.now();

    // 1. Cheack last OTP of user to check if 60 secons passed already from last sent
    Optional<Otp> lastOtpEntity = otpRepository.findTopByUserOrderByExpiresAtDesc(user); // can be null

    lastOtpEntity.ifPresent(
        lastOtp -> {
          // If OTP not used & not expired: Has to pass 60 seconds from last sent OTP (avoid spam)
          if (!lastOtp.isUsed()
              && lastOtp.getExpiresAt().isAfter(now)
              && lastOtp.getLastSentAt().plusSeconds(60).isAfter(now)) {
            long timeLeft = Duration.between(now, lastOtp.getLastSentAt().plusSeconds(60)).toSeconds();
            log.warn("⏳ Intento de spam OTP para usuario {}. Tiempo restante: {}s", user.getId(), timeLeft);
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

    log.info("🆕 [OTP_SERVICE] Nuevo OTP generado para el usuario [{}]. Expira: {}", 
        user.getId(), newOtp.getExpiresAt());

    return otpPlain;
    // FUTURE: Send otpPlain via WhatsApp/SMS/email
  }

  @Transactional
  public void validateOtp(User user, String plainOtp) {

    LocalDateTime now = LocalDateTime.now();

    log.debug("🔍 [OTP_SERVICE] Validando OTP para usuario [{}]... (Now: {})", user.getId(), now);

    // Diagnóstico extra: Ver si existe algún OTP (aunque esté expirado o usado)
    Optional<Otp> latest = otpRepository.findTopByUserOrderByExpiresAtDesc(user);
    if (latest.isPresent()) {
      Otp o = latest.get();
      log.debug("   - OTP más reciente: ID={}, Expira={}, Usado={}, Expirado={}", 
          o.getId(), o.getExpiresAt(), o.isUsed(), o.getExpiresAt().isBefore(now));
    } else {
      log.warn("   - [!] No se ha encontrado NINGÚN OTP para el usuario [{}]", user.getId());
    }

    // 1. Find the last NO expired/used OTP
    Otp lastOtpEntity = otpRepository
        .findActiveOtp(user, now)
        .orElseThrow(() -> {
            log.warn("❌ Intento de validación fallido: OTP no encontrado o expirado para el usuario {}", user.getId());
            return new RuntimeException("OTP not found or expired");
        });

    // 2. Control of total attemps from all the OTPs in total made by the user
    Integer recentAttempts = otpRepository.countRecentAttempts(user, now.minusMinutes(10));
    if (recentAttempts != null && recentAttempts >= 10) {
      log.error("🛑 BLOQUEO: Demasiados intentos fallidos (10+) en 10 min para el usuario {}", user.getId());
      throw new RuntimeException("Too many verification attempts. Try later.");
    }

    // 3. Control max attempts of the last specific OTP
    if (lastOtpEntity.getAttempts() >= 5) {
      lastOtpEntity.setUsed(true); // Block OTP for security
      log.error("🛑 BLOQUEO: OTP ID={} quemado tras 5 intentos para el usuario {}", lastOtpEntity.getId(), user.getId());
      throw new RuntimeException("Too many OTP attempts for this code.");
    }

    // 4. 5. Compare OTPhash db VS OTPplain client
    if (!passwordEncoder.matches(plainOtp, lastOtpEntity.getCode())) {
      lastOtpEntity.setAttempts(lastOtpEntity.getAttempts() + 1);
      log.warn("⚠️ OTP inválido para usuario {}. Intento: {}", user.getId(), lastOtpEntity.getAttempts());
      throw new RuntimeException("Invalid OTP");
    }

    // 5. Todo OK: Marcar como usado
    lastOtpEntity.setUsed(true);
    log.info("✅ OTP validado con éxito para el usuario {}", user.getId());
  }
}
