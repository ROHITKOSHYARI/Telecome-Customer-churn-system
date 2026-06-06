package com.customerChurn.entity;
import com.customerChurn.Enum.Churn;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
@Data
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String customerId;
    private String gender;
    private Integer seniorCitizen;
    private String partner;
    private String dependents;
    private Integer tenure;
    private String phoneService;
    private String multipleLines;
    private String internetService;
    private String onlineSecurity;
    private String onlineBackup;
    private String deviceProtection;
    private String techSupport;
    private String streamingTV;
    private String streamingMovies;
    private String contract;
    private String paperlessBilling;
    private String paymentMethod;
    private BigDecimal monthlyCharges;
    private BigDecimal totalCharges;
    @Enumerated(EnumType.STRING)
    private Churn churn;
    private Double churnProbability;
}