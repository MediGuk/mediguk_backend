package com.mediguk.backend.util;

import com.mediguk.backend.entity.Otp;
import com.mediguk.backend.entity.User;
import java.time.LocalDateTime;

public class TestDataFactory {

  public static User createUser(String dni) {
    User user = new User();
    user.setDocumentNumber(dni);
    // Añade aquí campos obligatorios si tu entidad User los tiene (email, nombre, etc.)
    return user;
  }

  public static Otp createOtp(User user, String hashedCode) {
    Otp otp = new Otp();
    otp.setUser(user);
    otp.setCode(hashedCode);
    otp.setCreatedAt(LocalDateTime.now());
    otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
    otp.setUsed(false);
    otp.setAttempts(0);
    return otp;
  }
}
