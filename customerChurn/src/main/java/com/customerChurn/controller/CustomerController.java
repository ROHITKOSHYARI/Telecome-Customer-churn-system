package com.customerChurn.controller;
import com.customerChurn.Enum.Churn;
import com.customerChurn.entity.Customer;
import com.customerChurn.repository.CustomerRepositories;
import com.customerChurn.service.Customerservice;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.CustomObjectInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    Customerservice customerservice;

    @PostMapping("/newUser")
    public ResponseEntity<?> saveUser(@RequestBody Customer customer){
        try{
            customerservice.saveUser(customer);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch (Exception e){
            log.error("customer not saved");
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }
    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        try{
            customerservice.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e){
            log.error("User cannot be deleted");
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @PutMapping("/UpdateUser/{id}")
    public ResponseEntity<?> updateuser(@PathVariable Long id, @RequestBody Customer customer){
        try{
            Customer customerold = customerservice.getCustomerById(id);
            customerservice.updateCustomer(customer, id);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch(Exception e){
            log.error("Customer not updated");
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping("/get_all")
    public ResponseEntity<?> getalluser(){
        try{
            List<Customer> list = new ArrayList<>();
            list = customerservice.getalluser();
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("cant fetch all user");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<?> getuser(@PathVariable Long id){
        try{
            Customer customer = customerservice.getUser(id);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("Cannot fetch Customer");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping
    public ResponseEntity<?> customerChurn(Long id){
        try{
            Churn churn = customerservice.getChurn(id);
            return new ResponseEntity<>(churn, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("Cannot find customer churn fot the current user");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
