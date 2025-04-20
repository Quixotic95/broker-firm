package com.github.quixotic95.brokerfirmchallenge.service.impl;

import com.github.quixotic95.brokerfirmchallenge.exception.InvalidException;
import com.github.quixotic95.brokerfirmchallenge.exception.NotFoundException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import com.github.quixotic95.brokerfirmchallenge.repository.CustomerRepository;
import com.github.quixotic95.brokerfirmchallenge.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    @Override
    public Customer findByUsername(String username) {
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    @Override
    public void validateCredentials(String username, String password) {
        Customer customer = findByUsername(username);
        if (!customer.getPassword()
                .equals(password)) { // şimdilik plaintext, sonra hash'e geçilebilir
            throw new InvalidException(ErrorCode.LOGIN_FAILED);
        }
    }
}
