package com.customerChurn.controller;
import com.customerChurn.dto.PredectionResponse;
import com.customerChurn.entity.Customer;

import com.customerChurn.service.PredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    private final PredictionService predictionService;
    @PostMapping("/getpredection")
    public ResponseEntity<?> getResponse(@RequestBody Customer customer){
        try {
            PredectionResponse predectionResponse = predictionService.predectionResponse(customer);
            return new ResponseEntity<>(predectionResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("cannot predect user : ",e);
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }
}
