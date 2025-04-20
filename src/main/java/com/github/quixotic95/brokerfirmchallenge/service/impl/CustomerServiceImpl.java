package com.github.quixotic95.brokerfirmchallenge.service.impl;

import com.github.quixotic95.brokerfirmchallenge.exception.InvalidException;
import com.github.quixotic95.brokerfirmchallenge.exception.NotFoundException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import com.github.quixotic95.brokerfirmchallenge.repository.CustomerRepository;
import com.github.quixotic95.brokerfirmchallenge.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            throw new NotFoundException(ErrorCode.CUSTOMER_NOT_FOUND);
        }
        return customers;
    }

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
        if (!passwordEncoder.matches(password, customer.getPassword())) {
            throw new InvalidException(ErrorCode.LOGIN_FAILED);
        }
    }

}
