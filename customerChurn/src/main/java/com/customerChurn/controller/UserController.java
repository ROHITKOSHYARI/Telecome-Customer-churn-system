package com.customerChurn.controller;

import com.customerChurn.dto.ChangePasswordRequest;
import com.customerChurn.dto.UserResponse;
import com.customerChurn.entity.User;
import com.customerChurn.service.Userservice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final Userservice userservice;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/getuser")
    public ResponseEntity<?> getuser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            UserResponse userResponse = userservice.getUserResponse(username);
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("cannot find the user");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/updateuser")
    public ResponseEntity<?> updateUser(@RequestBody UserResponse updatedUser){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userservice.getUser(username);
            user.setEmail(updatedUser.getEmail());
            user.setRoles(updatedUser.getRoles());
            userservice.updateUser(user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("cannot update the user", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PutMapping("/changepassword")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userservice.getUser(username);
            String hashpass = user.getPassword();
            if(!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())){
                return new ResponseEntity<>("confirm password does not match ",HttpStatus.BAD_GATEWAY);
            }
            if(!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(),hashpass)){
                return new ResponseEntity<>("your current password docent matches",HttpStatus.BAD_GATEWAY);
            }
            userservice.changepassword(changePasswordRequest, user);
            return new ResponseEntity<>("password changed successfully ",HttpStatus.OK);
        } catch (Exception e) {
            log.error("password not change",e);
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @DeleteMapping("/delete_user")
    public ResponseEntity<?> deleteuser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            userservice.deleteuser(username);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("not deleted : ", e);
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }
}
