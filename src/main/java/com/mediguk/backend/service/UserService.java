package com.mediguk.backend.service;

import com.mediguk.backend.dto.CreateUserDTO;
import com.mediguk.backend.entity.User;
import com.mediguk.backend.repository.UserRepository;
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
}
