package com.customerChurn.service;

import com.customerChurn.entity.User;
import com.customerChurn.repository.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Userservice {
    @Autowired
    UserRepositories userRepositories;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public void createUser(User user){
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepositories.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
}
