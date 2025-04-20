package com.github.quixotic95.brokerfirmchallenge.service;

import com.github.quixotic95.brokerfirmchallenge.model.Customer;

public interface CustomerService {

    Customer findById(Long id);

    Customer findByUsername(String username);

    void validateCredentials(String username, String password);
}
