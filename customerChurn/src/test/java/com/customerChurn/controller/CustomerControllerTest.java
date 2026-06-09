package com.customerChurn.controller;

import com.customerChurn.dto.PredectionResponse;
import com.customerChurn.entity.Customer;
import com.customerChurn.service.PredictionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private PredictionService predictionService;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void getResponseReturnsPrediction() throws Exception {
        PredectionResponse response = new PredectionResponse();
        response.setChurn("Yes");
        response.setChurnProbability(new BigDecimal("0.78"));
        when(predictionService.predectionResponse(any(Customer.class))).thenReturn(response);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

        mockMvc.perform(post("/customer/getpredection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(predictionRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.churn").value("Yes"))
                .andExpect(jsonPath("$.churnProbability").value(0.78));
    }

    @Test
    void getResponseReturnsBadGatewayWhenPredictionServiceFails() throws Exception {
        when(predictionService.predectionResponse(any(Customer.class))).thenThrow(new RuntimeException("ml unavailable"));
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

        mockMvc.perform(post("/customer/getpredection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(predictionRequestJson()))
                .andExpect(status().isBadGateway());
    }

    private String predictionRequestJson() {
        return """
                {
                  "gender": "Female",
                  "seniorCitizen": 0,
                  "partner": "Yes",
                  "dependents": "No",
                  "tenure": 12,
                  "phoneService": "Yes",
                  "multipleLines": "No",
                  "internetService": "Fiber optic",
                  "onlineSecurity": "No",
                  "onlineBackup": "Yes",
                  "deviceProtection": "No",
                  "techSupport": "No",
                  "streamingTV": "Yes",
                  "streamingMovies": "Yes",
                  "contract": "Month-to-month",
                  "paperlessBilling": "Yes",
                  "paymentMethod": "Electronic check",
                  "monthlyCharges": 89.10,
                  "totalCharges": 1069.20
                }
                """;
    }
}
