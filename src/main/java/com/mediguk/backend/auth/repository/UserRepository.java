package com.mediguk.backend.auth.repository;

import com.mediguk.backend.auth.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data JPA automatically generates the query (not execute) when it starts server
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByDocumentNumber(String documentNumber);
}
