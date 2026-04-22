package com.mediguk.backend.auth.service;

import com.mediguk.backend.auth.entity.User;

public interface OtpService {
    String requestOtp(User user);

    void validateOtp(User user, String plainOtp);
}
