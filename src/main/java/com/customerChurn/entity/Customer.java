package com.customerChurn.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private Integer tenure;
    private BigDecimal monthlyCharge;
    private String contractType;
    private String internetService;
    private Integer supportCalls;
    private Boolean churn;
}
