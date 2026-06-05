package com.customerChurn.service;

import com.customerChurn.entity.User;
import com.customerChurn.repository.CustomerRepositories;
import com.customerChurn.repository.UserRepositories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserServiceIMPL implements UserDetailsService{

    @Autowired
    UserRepositories userRepositories;

    public UserDetails loadUserByUsername(String username){
        User user = userRepositories.findByUsername(username);
        if(user!= null){
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRoles().toArray(new String[0]))
                    .build();
        }
        log.error("User not found with this username{}", username);
        throw new UsernameNotFoundException("User not found with this username" + username);
    }
}
