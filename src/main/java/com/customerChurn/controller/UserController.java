package com.customerChurn.controller;

import com.customerChurn.entity.User;
import com.customerChurn.service.Userservice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final Userservice userservice;

    @GetMapping("/getuser")
    public ResponseEntity<?> getuser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userservice.getUser(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            log.error("cannot find the user");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
