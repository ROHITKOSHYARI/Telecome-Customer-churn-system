package com.customerChurn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "User")
public class User {

    @Id
    private Long id;
    private String UserName;
    private String Password;
    private String Email;
    private List<String> roles;

}
