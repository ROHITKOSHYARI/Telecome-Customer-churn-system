package com.customerChurn.repository;

import com.customerChurn.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepositories extends JpaRepository<Customer,Long> {
}
