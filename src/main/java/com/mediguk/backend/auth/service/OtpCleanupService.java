package com.mediguk.backend.auth.service;

import com.mediguk.backend.auth.repository.OtpRepository;
import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OtpCleanupService {

  private final OtpRepository otpRepository;

  public OtpCleanupService(OtpRepository otpRepository) {
    this.otpRepository = otpRepository;
  }

  @Transactional
  @Scheduled(cron = "0 0 * * * *") // SCHEDULED CRON
  public void cleanExpiredOtps() {
    System.out.println("Cleaning expired OTPs...");
    otpRepository.deleteExpiredOtps(LocalDateTime.now());
  }
}
