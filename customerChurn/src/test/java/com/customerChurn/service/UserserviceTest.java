package com.customerChurn.service;

import com.customerChurn.dto.ChangePasswordRequest;
import com.customerChurn.dto.UserResponse;
import com.customerChurn.entity.User;
import com.customerChurn.repository.UserRepositories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserserviceTest {

    @Mock
    private UserRepositories userRepositories;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private Userservice userservice;

    @Test
    void createUserEncodesPasswordBeforeSaving() {
        User user = new User();
        user.setId(1L);
        user.setUsername("demo");
        user.setPassword("plain-password");
        user.setRoles(List.of("USER"));
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");

        userservice.createUser(user);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepositories).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void getUserResponseMapsEntityToUserResponseWithoutPassword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("demo");
        user.setPassword("encoded-password");
        user.setEmail("demo@example.com");
        user.setRoles(List.of("USER"));
        when(userRepositories.findByUsername("demo")).thenReturn(user);

        UserResponse response = userservice.getUserResponse("demo");

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("demo");
        assertThat(response.getEmail()).isEqualTo("demo@example.com");
        assertThat(response.getRoles()).containsExactly("USER");
    }

    @Test
    void deleteuserDeletesByUsername() {
        userservice.deleteuser("demo");

        verify(userRepositories).deleteUserByUsername("demo");
    }

    @Test
    void changepasswordEncodesConfirmPasswordAndUpdatesUser() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("oldPassword");
        changePasswordRequest.setNewPassword("newPassword");
        changePasswordRequest.setConfirmPassword("newPassword");

        User user = new User();
        user.setId(1L);
        user.setUsername("demo");
        user.setPassword("encoded-old-password");

        when(passwordEncoder.encode("newPassword")).thenReturn("encoded-new-password");

        userservice.changepassword(changePasswordRequest, user);

        assertThat(user.getPassword()).isEqualTo("encoded-new-password");
    }

    @Test
    void updateUserSavesWithoutEncodingExistingPassword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("demo");
        user.setPassword("already-encoded-password");
        user.setEmail("new@example.com");
        user.setRoles(List.of("USER"));

        userservice.updateUser(user);

        verify(userRepositories).save(user);
        verify(passwordEncoder, never()).encode(anyString());
    }
}
