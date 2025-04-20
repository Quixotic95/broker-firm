package com.github.quixotic95.brokerfirmchallenge.controller;

import com.github.quixotic95.brokerfirmchallenge.aspect.AuthorizeEndpoint;
import com.github.quixotic95.brokerfirmchallenge.dto.response.CustomerDto;
import com.github.quixotic95.brokerfirmchallenge.mapper.CustomerMapper;
import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import com.github.quixotic95.brokerfirmchallenge.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Admin-only customer listing and detail operations")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @Operation(summary = "Get customer by ID", description = "Returns a customer by ID. Only accessible to admins.")
    @ApiResponses(
            {@ApiResponse(responseCode = "200", description = "Customer found and returned"),
                    @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied for non-admins", content = @Content)})
    @GetMapping("/{id}")
    @AuthorizeEndpoint(customerAccessible = false)
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        return ResponseEntity.ok(customerMapper.toDto(customer));
    }

    @Operation(summary = "List all customers", description = "Returns a list of all customers. Only accessible to admins.")
    @ApiResponses(
            {@ApiResponse(responseCode = "200", description = "Customer list returned successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied for non-admins", content = @Content)})
    @GetMapping
    @AuthorizeEndpoint(customerAccessible = false)
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<Customer> customers = customerService.findAll();
        return ResponseEntity.ok(customerMapper.toDtoList(customers));
    }
}