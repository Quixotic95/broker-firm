package com.github.quixotic95.brokerfirmchallenge.service;

import com.github.quixotic95.brokerfirmchallenge.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> findAll();

    Customer findById(Long id);

    Customer findByUsername(String username);

    void validateCredentials(String username, String password);
}
