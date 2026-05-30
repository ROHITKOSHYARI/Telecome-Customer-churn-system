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
        if(customer.getAge() != null) existing.setAge(customer.getAge());
        if(customer.getName() != null) existing.setName(customer.getName());
        if(customer.getGender()!= null) existing.setGender(customer.getGender());
        if(customer.getTenure()!= null) existing.setTenure(customer.getTenure());
        if(customer.getMonthlyCharge()!=null) existing.setMonthlyCharge(customer.getMonthlyCharge());
        if(customer.getContractType()!=null) existing.setContractType(customer.getContractType());
        if(customer.getInternetService()!=null) existing.setInternetService(customer.getInternetService());
        if(customer.getSupportCalls()!= null) existing.setSupportCalls(customer.getSupportCalls());
        if(customer.getChurn()!= null) existing.setChurn(customer.getChurn());
        customerRepositories.save(existing);
    }

    public List<Customer> getalluser(){
        return customerRepositories.findAll();
    }

}
