package com.mediguk.backend.service;

import com.mediguk.backend.dto.CreateUserDTO;
import com.mediguk.backend.entity.User;
import com.mediguk.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User createUser(CreateUserDTO dto) {

    User user = new User();

    user.setEmail(dto.getEmail());
    user.setPassword(dto.getPassword());
    user.setPhoneNumber(dto.getPhoneNumber());

    String hashedPassword = passwordEncoder.encode(user.getPassword());

    user.setPassword(hashedPassword);

    return userRepository.save(user);
  }
}
