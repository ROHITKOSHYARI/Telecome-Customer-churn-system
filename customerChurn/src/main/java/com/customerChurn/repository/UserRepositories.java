package com.customerChurn.repository;

import com.customerChurn.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositories extends JpaRepository<User, Long> {
    User findByUsername(String username);
    void deleteUserByUsername(String username);
}
