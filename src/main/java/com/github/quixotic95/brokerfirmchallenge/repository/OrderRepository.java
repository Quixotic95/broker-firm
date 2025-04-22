package com.github.quixotic95.brokerfirmchallenge.repository;

import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(
            "SELECT o FROM Order o WHERE " + "(:customerId IS NULL OR o.customerId = :customerId) AND " + "(:assetName IS NULL OR o.assetName = :assetName) AND " + "(:status IS NULL OR o.status = :status) AND " + "(:startDate IS NULL OR o.createDate >= :startDate) AND " + "(:endDate IS NULL OR o.createDate <= :endDate)")
    List<Order> findAllByFilters(
            @Param("customerId") Long customerId,
            @Param("assetName") String assetName,
            @Param("status") OrderStatus status, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithLock(@Param("id") Long id);

}