package com.customerChurn.service;

import com.customerChurn.entity.User;
import com.customerChurn.repository.UserRepositories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceIMPLTest {

    @Mock
    private UserRepositories userRepositories;

    @InjectMocks
    private UserServiceIMPL userServiceIMPL;

    @Test
    void loadUserByUsernameReturnsSpringSecurityUserDetails() {
        User user = new User();
        user.setUsername("demo");
        user.setPassword("encoded-password");
        user.setRoles(List.of("USER"));
        when(userRepositories.findByUsername("demo")).thenReturn(user);

        UserDetails userDetails = userServiceIMPL.loadUserByUsername("demo");

        assertThat(userDetails.getUsername()).isEqualTo("demo");
        assertThat(userDetails.getPassword()).isEqualTo("encoded-password");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsernameThrowsWhenUserDoesNotExist() {
        when(userRepositories.findByUsername("missing")).thenReturn(null);

        assertThatThrownBy(() -> userServiceIMPL.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("missing");
    }
}
