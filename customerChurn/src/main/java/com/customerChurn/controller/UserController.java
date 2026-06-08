package com.customerChurn.controller;

import com.customerChurn.entity.User;
import com.customerChurn.service.Userservice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
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

    @DeleteMapping("/delete_user")
    public ResponseEntity<?> deleteuser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            userservice.deleteuser(username);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("not deleted : ", e);
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }
}
