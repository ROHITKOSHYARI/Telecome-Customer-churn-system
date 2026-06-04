package com.customerChurn.controller;

import com.customerChurn.entity.User;
import com.customerChurn.service.Userservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller("/public")
public class Public {
    @Autowired
    Userservice userservice;

    @PostMapping("saveUser")
    public void SaveUser(@RequestBody User user){
        try {
            userservice.createUser(user);
        }
        catch (Exception e){
            log.error("cannot create the user");
        }
    }
}
