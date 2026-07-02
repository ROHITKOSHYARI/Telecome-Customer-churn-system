package com.customerChurn.controller;

import com.customerChurn.dto.LoginRequest;
import com.customerChurn.dto.LoginResponse;
import com.customerChurn.dto.UserResponse;
import com.customerChurn.entity.User;
import com.customerChurn.service.JwtService;
import com.customerChurn.service.Userservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/public")
public class Public {

    private final Userservice userservice;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public Public(Userservice userservice, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userservice = userservice;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
            log.error("cannot create the user",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Controller username = [" + loginRequest.getUsername() + "]");
        System.out.println("Controller password = [" + loginRequest.getPassword() + "]");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserResponse userResponse = userservice.getUserResponse(userDetails.getUsername());
            LoginResponse loginResponse = new LoginResponse(
                    jwtService.generateToken(userDetails),
                    "Bearer",
                    jwtService.getExpirationMs(),
                    userResponse
            );
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("cannot login user", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
