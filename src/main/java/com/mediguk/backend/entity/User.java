package com.mediguk.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import lombok.Data; // Generates getters, setters, constructor and equals/hashCode automatically 
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Generate instancies manually

import java.util.UUID;

@Entity
@Data //get*, set*, constructor, equals() y hashCode()
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String email;

    private String password;

    private String phoneNumber;

}
