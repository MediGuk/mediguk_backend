package com.mediguk.backend.auth.service;

import com.mediguk.backend.auth.dto.request.CreateUserDTO;
import com.mediguk.backend.auth.entity.User;

public interface UserService {

    User createUser(CreateUserDTO dto);

    User getUserByDocument(String dni);
}
