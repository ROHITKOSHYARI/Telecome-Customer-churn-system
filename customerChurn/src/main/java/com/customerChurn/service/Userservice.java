package com.customerChurn.service;

import com.customerChurn.dto.ChangePasswordRequest;
import com.customerChurn.dto.UserResponse;
import com.customerChurn.entity.User;
import com.customerChurn.repository.UserRepositories;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class Userservice {

    private final UserRepositories userRepositories;
    private final PasswordEncoder passwordEncoder;
    public void createUser(User user){
        try {
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.setRoles(java.util.List.of("USER"));
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepositories.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserResponse getUserResponse(String username){
        User user =  userRepositories.findByUsername(username);
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRoles(user.getRoles());
        return userResponse;
    }

    public void changepassword(ChangePasswordRequest changePasswordRequest, User user){
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getConfirmPassword()));
        userRepositories.save(user);
    }

    public void updateUser(User user){
        userRepositories.save(user);
    }

    public User getUser(String username){
        return userRepositories.findByUsername(username);
    }

    @Transactional
    public void deleteuser(String username){
        userRepositories.deleteUserByUsername(username);
    }
}
