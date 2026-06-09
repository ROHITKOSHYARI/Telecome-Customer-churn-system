package com.customerChurn.controller;

import com.customerChurn.entity.User;
import com.customerChurn.service.Userservice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PublicControllerTest {

    @Mock
    private Userservice userservice;

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
}
