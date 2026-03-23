package com.mediguk.backend.auth.controller;

import com.mediguk.backend.auth.dto.request.CreateUserDTO;
import com.mediguk.backend.auth.entity.User;
import com.mediguk.backend.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public User createUser(@Valid @RequestBody CreateUserDTO dto) {
    return userService.createUser(dto);
  }
}
