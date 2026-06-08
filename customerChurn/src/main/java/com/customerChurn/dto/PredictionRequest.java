package com.customerChurn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PredictionRequest {

    private String gender;

    @JsonProperty("SeniorCitizen")
    private Integer seniorCitizen;

    @JsonProperty("Partner")
    private String partner;

    @JsonProperty("Dependents")
    private String dependents;

    private Integer tenure;

    @JsonProperty("PhoneService")
    private String phoneService;

    @JsonProperty("MultipleLines")
    private String multipleLines;

    @JsonProperty("InternetService")
    private String internetService;

    @JsonProperty("OnlineSecurity")
    private String onlineSecurity;

    @JsonProperty("OnlineBackup")
    private String onlineBackup;

    @JsonProperty("DeviceProtection")
    private String deviceProtection;

    @JsonProperty("TechSupport")
    private String techSupport;

    @JsonProperty("StreamingTV")
    private String streamingTV;

    @JsonProperty("StreamingMovies")
    private String streamingMovies;

    @JsonProperty("Contract")
    private String contract;

    @JsonProperty("PaperlessBilling")
    private String paperlessBilling;

    @JsonProperty("PaymentMethod")
    private String paymentMethod;

    @JsonProperty("MonthlyCharges")
    private BigDecimal monthlyCharges;

    @JsonProperty("TotalCharges")
    private BigDecimal totalCharges;
}