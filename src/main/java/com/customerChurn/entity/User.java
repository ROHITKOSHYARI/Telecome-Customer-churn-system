package com.customerChurn.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id;
    private String username;
    private String password;
    private String email;
    @ElementCollection
    private List<String> roles;

}
