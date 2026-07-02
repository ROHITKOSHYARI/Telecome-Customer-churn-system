package com.customerChurn.controller;

import com.customerChurn.dto.UserResponse;
import com.customerChurn.entity.User;
import com.customerChurn.service.JwtService;
import com.customerChurn.service.Userservice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PublicControllerTest {

    @Mock
    private Userservice userservice;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private Public publicController;

    @Test
    void healthcheckReturnsOk() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();

        mockMvc.perform(post("/public/health_check"))
                .andExpect(status().isOk())
                .andExpect(content().string("health check pass "));
    }

    @Test
    void saveUserCreatesUser() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();

        mockMvc.perform(post("/public/saveuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 1,
                                  "username": "demo",
                                  "password": "secret",
                                  "email": "demo@example.com",
                                  "roles": ["USER"]
                                }
                                """))
                .andExpect(status().isCreated());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userservice).createUser(userCaptor.capture());
        assertThat(userCaptor.getValue().getUsername()).isEqualTo("demo");
        assertThat(userCaptor.getValue().getRoles()).containsExactly("USER");
    }

    @Test
    void saveUserReturnsBadRequestWhenServiceFails() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
        doThrow(new RuntimeException("duplicate user")).when(userservice).createUser(any(User.class));

        mockMvc.perform(post("/public/saveuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 1,
                                  "username": "demo",
                                  "password": "secret",
                                  "email": "demo@example.com",
                                  "roles": ["USER"]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginReturnsJwtTokenAndUserDetails() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("demo")
                .password("encoded-password")
                .roles("USER")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("demo");
        userResponse.setEmail("demo@example.com");
        userResponse.setRoles(List.of("USER"));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userservice.getUserResponse("demo")).thenReturn(userResponse);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        mockMvc.perform(post("/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "demo",
                                  "password": "secret"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(86400000))
                .andExpect(jsonPath("$.user.username").value("demo"));
    }

    @Test
    void loginReturnsUnauthorizedForInvalidCredentials() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        mockMvc.perform(post("/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "demo",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }
}
