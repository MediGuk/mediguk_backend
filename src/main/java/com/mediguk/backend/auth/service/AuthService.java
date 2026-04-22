package com.mediguk.backend.auth.service;

import com.mediguk.backend.auth.dto.request.RequestOtpDTO;
import com.mediguk.backend.auth.dto.request.VerifyOtpDTO;
import com.mediguk.backend.auth.model.AuthResult;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    String requestLogin(RequestOtpDTO dto);

    AuthResult verifyLogin(VerifyOtpDTO dto, HttpServletRequest request);

    void logout(String accessToken);

    AuthResult refresh(String actualRefreshToken, String fingerprintRaw);
}
