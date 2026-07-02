package com.customerChurn.service;

import com.customerChurn.dto.PredectionResponse;
import com.customerChurn.dto.PredictionRequest;
import com.customerChurn.entity.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredictionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PredictionService predictionService;

    @Test
    void predectionResponseMapsCustomerAndCallsFastApiService() throws Exception {

        ReflectionTestUtils.setField(
                predictionService,
                "url",
                "http://churn-ml:8000/predict"
        );

        PredectionResponse expectedResponse = new PredectionResponse();
        expectedResponse.setChurn("Yes");
        expectedResponse.setChurnProbability(new BigDecimal("0.78"));
        when(restTemplate.postForEntity(
                eq("http://churn-ml:8000/predict"),
                org.mockito.ArgumentMatchers.any(HttpEntity.class),
                eq(PredectionResponse.class)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        PredectionResponse actualResponse = predictionService.predectionResponse(customer());

        assertThat(actualResponse).isSameAs(expectedResponse);
        ArgumentCaptor<HttpEntity<PredictionRequest>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(
                eq("http://churn-ml:8000/predict"),
                entityCaptor.capture(),
                eq(PredectionResponse.class));

        HttpEntity<PredictionRequest> requestEntity = entityCaptor.getValue();
        assertThat(requestEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        PredictionRequest request = requestEntity.getBody();
        assertThat(request).isNotNull();
        assertThat(request.getGender()).isEqualTo("Female");
        assertThat(request.getSeniorCitizen()).isZero();
        assertThat(request.getInternetService()).isEqualTo("Fiber optic");
        assertThat(request.getMonthlyCharges()).isEqualByComparingTo("89.10");
        assertThat(request.getTotalCharges()).isEqualByComparingTo("1069.20");
    }

    private Customer customer() {
        Customer customer = new Customer();
        customer.setGender("Female");
        customer.setSeniorCitizen(0);
        customer.setPartner("Yes");
        customer.setDependents("No");
        customer.setTenure(12);
        customer.setPhoneService("Yes");
        customer.setMultipleLines("No");
        customer.setInternetService("Fiber optic");
        customer.setOnlineSecurity("No");
        customer.setOnlineBackup("Yes");
        customer.setDeviceProtection("No");
        customer.setTechSupport("No");
        customer.setStreamingTV("Yes");
        customer.setStreamingMovies("Yes");
        customer.setContract("Month-to-month");
        customer.setPaperlessBilling("Yes");
        customer.setPaymentMethod("Electronic check");
        customer.setMonthlyCharges(new BigDecimal("89.10"));
        customer.setTotalCharges(new BigDecimal("1069.20"));
        return customer;
    }
}
