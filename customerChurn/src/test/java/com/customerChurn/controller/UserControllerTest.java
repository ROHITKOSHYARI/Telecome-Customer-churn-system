package com.customerChurn.controller;

import com.customerChurn.dto.UserResponse;
import com.customerChurn.service.Userservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private Userservice userservice;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getuserReturnsAuthenticatedUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("demo", "password"));
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setUsername("demo");
        response.setEmail("demo@example.com");
        response.setRoles(List.of("USER"));
        when(userservice.getUserResponse("demo")).thenReturn(response);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        mockMvc.perform(get("/user/getuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("demo"))
                .andExpect(jsonPath("$.email").value("demo@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("USER"));
    }

    @Test
    void getuserReturnsNotFoundWhenServiceFails() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("missing", "password"));
        when(userservice.getUserResponse("missing")).thenThrow(new RuntimeException("not found"));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        mockMvc.perform(get("/user/getuser"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteuserDeletesAuthenticatedUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("demo", "password"));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        mockMvc.perform(delete("/user/delete_user"))
                .andExpect(status().isNoContent());

        verify(userservice).deleteuser("demo");
    }

    @Test
    void deleteuserReturnsBadGatewayWhenServiceFails() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("demo", "password"));
        doThrow(new RuntimeException("delete failed")).when(userservice).deleteuser("demo");
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        mockMvc.perform(delete("/user/delete_user"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void updatePasswordSucceedsWhenCurrentPasswordMatchesAndPasswordsMatch() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("demo", "password"));

        com.customerChurn.dto.ChangePasswordRequest changePasswordRequest = new com.customerChurn.dto.ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("password");
        changePasswordRequest.setNewPassword("newpassword");
        changePasswordRequest.setConfirmPassword("newpassword");

        com.customerChurn.entity.User user = new com.customerChurn.entity.User();
        user.setUsername("demo");
        user.setPassword("encoded-password");

        when(userservice.getUser("demo")).thenReturn(user);
        when(passwordEncoder.matches("password", "encoded-password")).thenReturn(true);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        mockMvc.perform(put("/user/changepassword")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string("password changed successfully "));

        verify(userservice).changepassword(changePasswordRequest, user);
    }

    @Test
    void updatePasswordReturnsBadGatewayWhenServiceFails() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("demo", "password"));
        
        com.customerChurn.dto.ChangePasswordRequest changePasswordRequest = new com.customerChurn.dto.ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("password");
        changePasswordRequest.setNewPassword("newpassword");
        changePasswordRequest.setConfirmPassword("newpassword");
        
        when(userservice.getUser("demo")).thenThrow(new RuntimeException("error"));
        
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        
        mockMvc.perform(put("/user/changepassword")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadGateway());
    }
}
