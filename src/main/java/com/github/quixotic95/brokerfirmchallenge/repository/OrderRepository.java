package com.github.quixotic95.brokerfirmchallenge.repository;

import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithLock(@Param("id") Long id);

    List<Order> findAllByCustomerIdAndCreateDateBetween(Long customerId, Instant start, Instant end);

    List<Order> findAllByCustomerIdAndStatusAndCreateDateBetween(Long customerId, OrderStatus status, Instant start, Instant end);
}