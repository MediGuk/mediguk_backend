package com.mediguk.backend.core.utils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashUtil {
  
  public static String sha256(String rawString) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));
        
        // Convertimos los bytes locos a un texto hexadecimal limpio (String)
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
        
    } catch (Exception e) {
        throw new RuntimeException("Error fatal ejecutando SHA-256", e);
    }
  }
}
