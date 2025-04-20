package com.github.quixotic95.brokerfirmchallenge.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.quixotic95.brokerfirmchallenge.exception.InvalidException;
import com.github.quixotic95.brokerfirmchallenge.exception.NotFoundException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import com.github.quixotic95.brokerfirmchallenge.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .username("john")
                .password("hashed-password")
                .build();
    }

    @Test
    void findAll_shouldReturnList_whenCustomersExist() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<Customer> result = customerService.findAll();

        assertThat(result).containsExactly(customer);
    }

    @Test
    void findAll_shouldThrow_whenEmpty() {
        when(customerRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> customerService.findAll()).isInstanceOf(NotFoundException.class)
                .hasMessageContaining(ErrorCode.CUSTOMER_NOT_FOUND.getMessage());
    }

    @Test
    void findById_shouldReturnCustomer_whenExists() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = customerService.findById(1L);

        assertThat(result).isEqualTo(customer);
    }

    @Test
    void findById_shouldThrow_whenNotExists() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void findByUsername_shouldReturnCustomer_whenExists() {
        when(customerRepository.findByUsername("john")).thenReturn(Optional.of(customer));

        Customer result = customerService.findByUsername("john");

        assertThat(result).isEqualTo(customer);
    }

    @Test
    void findByUsername_shouldThrow_whenNotExists() {
        when(customerRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findByUsername("john")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void validateCredentials_shouldSucceed_whenValid() {
        when(customerRepository.findByUsername("john")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("raw-password", "hashed-password")).thenReturn(true);

        customerService.validateCredentials("john", "raw-password");

        verify(passwordEncoder).matches("raw-password", "hashed-password");
    }

    @Test
    void validateCredentials_shouldThrow_whenInvalid() {
        when(customerRepository.findByUsername("john")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("wrong", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> customerService.validateCredentials("john", "wrong")).isInstanceOf(InvalidException.class)
                .hasMessageContaining(ErrorCode.LOGIN_FAILED.getMessage());
    }
}
