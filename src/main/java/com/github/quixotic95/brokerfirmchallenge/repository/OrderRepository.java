package com.github.quixotic95.brokerfirmchallenge.repository;

import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(
            "SELECT o FROM Order o WHERE " + "(:customerId IS NULL OR o.customerId = :customerId) AND " + "(:status IS NULL OR o.status = :status) AND " + "(:startDate IS NULL OR o.createDate >= :startDate) AND " + "(:endDate IS NULL OR o.createDate <= :endDate)")
    List<Order> findAllByFilters(
            @Param("customerId") Long customerId,
            @Param("status") OrderStatus status, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    List<Order> findAllByCustomerIdAndStatusAndCreateDateBetween(Long customerId, OrderStatus status, Instant start, Instant end);

    List<Order> findAllByCustomerIdAndCreateDateBetween(Long customerId, Instant start, Instant end);
}