package com.customerChurn.service;

import com.customerChurn.entity.User;
import com.customerChurn.repository.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Userservice {

    private final UserRepositories userRepositories;
    private final PasswordEncoder passwordEncoder;
    public void createUser(User user){
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepositories.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User getUser(String username){
        return userRepositories.findByUsername(username);
    }

    @Transactional
    public void deleteuser(String username){
        userRepositories.deleteUserByUsername(username);
    }



}
