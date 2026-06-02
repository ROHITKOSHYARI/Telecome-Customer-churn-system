package com.customerChurn.service;

import com.customerChurn.entity.Customer;
import com.customerChurn.repository.CustomerRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Customerservice {

    @Autowired
    CustomerRepositories customerRepositories;

    public void saveUser(Customer customer){
        customerRepositories.save(customer);
    }

    public void deleteUser(Long id){
        customerRepositories.deleteById(id);
    }

    public Customer getCustomerById(Long id){
        return customerRepositories.getReferenceById(id);
    }

    public void updateCustomer(Customer customer, Long id){
        Customer existing = customerRepositories.getReferenceById(id);
        if(customer.getGender() != null) existing.setGender(customer.getGender());
        if(customer.getSeniorCitizen() != null) existing.setSeniorCitizen(customer.getSeniorCitizen());
        if(customer.getPartner() != null) existing.setPartner(customer.getPartner());
        if(customer.getDependents() != null) existing.setDependents(customer.getDependents());
        if(customer.getTenure() != null) existing.setTenure(customer.getTenure());
        if(customer.getPhoneService() != null) existing.setPhoneService(customer.getPhoneService());
        if(customer.getMultipleLines() != null) existing.setMultipleLines(customer.getMultipleLines());
        if(customer.getInternetService() != null) existing.setInternetService(customer.getInternetService());
        if(customer.getOnlineSecurity() != null) existing.setOnlineSecurity(customer.getOnlineSecurity());
        if(customer.getOnlineBackup() != null) existing.setOnlineBackup(customer.getOnlineBackup());
        if(customer.getDeviceProtection() != null) existing.setDeviceProtection(customer.getDeviceProtection());
        if(customer.getTechSupport() != null) existing.setTechSupport(customer.getTechSupport());
        if(customer.getStreamingTV() != null) existing.setStreamingTV(customer.getStreamingTV());
        if(customer.getStreamingMovies() != null) existing.setStreamingMovies(customer.getStreamingMovies());
        if(customer.getContract() != null) existing.setContract(customer.getContract());
        if(customer.getPaperlessBilling() != null) existing.setPaperlessBilling(customer.getPaperlessBilling());
        if(customer.getPaymentMethod() != null) existing.setPaymentMethod(customer.getPaymentMethod());
        if(customer.getMonthlyCharges() != null) existing.setMonthlyCharges(customer.getMonthlyCharges());
        if(customer.getTotalCharges() != null) existing.setTotalCharges(customer.getTotalCharges());
        if(customer.getChurn() != null) existing.setChurn(customer.getChurn());
        customerRepositories.save(existing);
    }

    public List<Customer> getalluser(){
        return customerRepositories.findAll();
    }

    public Customer getUser(Long id){
        return customerRepositories.getReferenceById(id);
    }

    public String getChurn(Long id){
        Customer customer = customerRepositories.getReferenceById(id);
        return customer.getChurn();
    }
}
