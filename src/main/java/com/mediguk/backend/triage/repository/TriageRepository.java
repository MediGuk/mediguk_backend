package com.mediguk.backend.triage.repository;

import com.mediguk.backend.triage.entity.TriageCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TriageRepository extends JpaRepository<TriageCase, UUID> {
    // Aquí ya tenemos save(), findById(), etc. ¡Dignidad Lógica!
}
