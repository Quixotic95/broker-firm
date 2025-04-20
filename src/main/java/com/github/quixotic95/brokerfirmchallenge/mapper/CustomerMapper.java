package com.github.quixotic95.brokerfirmchallenge.mapper;

import com.github.quixotic95.brokerfirmchallenge.dto.response.CustomerDto;
import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {
    public CustomerDto toDto(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getUsername(), customer.getRole());
    }

    public List<CustomerDto> toDtoList(List<Customer> customers) {
        return customers.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
