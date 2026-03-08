package com.mediguk.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MedigukBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(MedigukBackendApplication.class, args);
  }
}
