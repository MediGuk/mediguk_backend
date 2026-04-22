package com.mediguk.backend.auth.service;

import com.mediguk.backend.auth.entity.AuthSession;
import com.mediguk.backend.auth.entity.User;
import com.mediguk.backend.auth.model.SessionCreationResult;
import jakarta.servlet.http.HttpServletRequest;

public interface SessionService {

    SessionCreationResult createSession(User user, HttpServletRequest request);

    void revokeSession(String sessionToken);

    AuthSession getSessionByRefreshToken(String refreshToken);

    String rotateRefreshToken(AuthSession session);

    AuthSession getValidSession(String refreshToken, String fingerprintRaw);
}
