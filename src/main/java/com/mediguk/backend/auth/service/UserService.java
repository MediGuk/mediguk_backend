package com.mediguk.backend.auth.service;

import com.mediguk.backend.auth.dto.request.CreateUserDTO;
import com.mediguk.backend.auth.entity.User;
import com.mediguk.backend.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(CreateUserDTO dto) {

    User user = new User();

    user.setDocumentNumber(dto.documentNumber());
    user.setFullName(dto.fullName());
    user.setEmail(dto.email());
    user.setPhoneNumber(dto.phoneNumber());

    User savedUser = userRepository.save(user);
    return savedUser;

    // FUTURE: devuelve DTO !!!!!! or Record
  }

  public User getUserByDocument(String dni) {
    return userRepository
        .findByDocumentNumber(dni)
        .orElseThrow(() -> new RuntimeException("User not found with DNI: " + dni));
  }
}
