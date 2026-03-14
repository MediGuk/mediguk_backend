package com.mediguk.backend.auth.service;

import com.mediguk.backend.auth.dto.CreateUserDTO;
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

    user.setDocumentNumber(dto.getDocumentNumber());
    user.setFullName(dto.getFullName());
    user.setEmail(dto.getEmail());
    user.setPhoneNumber(dto.getPhoneNumber());

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
