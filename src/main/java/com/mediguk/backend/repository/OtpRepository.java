package com.mediguk.backend.repository;

import com.mediguk.backend.entity.Otp;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp, UUID> {}
