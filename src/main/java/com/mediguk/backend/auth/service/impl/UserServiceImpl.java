package com.mediguk.backend.auth.service.impl;

import com.mediguk.backend.auth.dto.request.CreateUserDTO;
import com.mediguk.backend.auth.entity.User;
import com.mediguk.backend.auth.repository.UserRepository;
import com.mediguk.backend.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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

