package com.customerChurn.service;

import com.customerChurn.dto.PredectionResponse;
import com.customerChurn.dto.PredictionRequest;
import com.customerChurn.entity.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PredictionService{
    private final RestTemplate restTemplate;

    @Value("${ml.service.url}")
    private String url;

    public PredectionResponse predectionResponse(Customer customer) throws JsonProcessingException {

        PredictionRequest request = new PredictionRequest();

        request.setGender(customer.getGender());
        request.setSeniorCitizen(customer.getSeniorCitizen());
        request.setPartner(customer.getPartner());
        request.setDependents(customer.getDependents());
        request.setTenure(customer.getTenure());
        request.setPhoneService(customer.getPhoneService());
        request.setMultipleLines(customer.getMultipleLines());
        request.setInternetService(customer.getInternetService());
        request.setOnlineSecurity(customer.getOnlineSecurity());
        request.setOnlineBackup(customer.getOnlineBackup());
        request.setDeviceProtection(customer.getDeviceProtection());
        request.setTechSupport(customer.getTechSupport());
        request.setStreamingTV(customer.getStreamingTV());
        request.setStreamingMovies(customer.getStreamingMovies());
        request.setContract(customer.getContract());
        request.setPaperlessBilling(customer.getPaperlessBilling());
        request.setPaymentMethod(customer.getPaymentMethod());
        request.setMonthlyCharges(customer.getMonthlyCharges());
        request.setTotalCharges(customer.getTotalCharges());



        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PredictionRequest> requestEntity  = new HttpEntity<>(request,headers);
        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<PredectionResponse> response =
                restTemplate.postForEntity(
                        url,
                        requestEntity,
                        PredectionResponse.class
                );
        return response.getBody();
    }
}
