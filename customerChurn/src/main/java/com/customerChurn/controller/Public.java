package com.customerChurn.controller;

import com.customerChurn.entity.User;
import com.customerChurn.service.Userservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/public")
public class Public {

    private final Userservice userservice;

    @Autowired
    public Public(Userservice userservice) {
        this.userservice = userservice;
    }

    @PostMapping("/health_check")
    public ResponseEntity<?> healthcheck(){
        try{
            return new ResponseEntity<>("health check pass ",HttpStatus.OK);
        }
        catch (Exception e){
            log.error("health check failed");
            return new ResponseEntity<>("Health check failed ",HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping("/saveuser")
    public ResponseEntity<?> saveUser(@RequestBody User user){
        try {
            userservice.createUser(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch (Exception e){
            log.error("cannot create the user");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}