package com.github.quixotic95.brokerfirmchallenge.repository;

import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
