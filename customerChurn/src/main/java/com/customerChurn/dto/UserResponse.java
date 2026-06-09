package com.customerChurn.dto;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    @Id
    private Long id;
    private String username;
    private String email;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;
}
