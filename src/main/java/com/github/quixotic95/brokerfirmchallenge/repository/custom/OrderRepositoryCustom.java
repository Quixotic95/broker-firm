package com.github.quixotic95.brokerfirmchallenge.repository.custom;

import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;
import com.github.quixotic95.brokerfirmchallenge.model.Order;

import java.time.Instant;
import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findAllByDynamicFilters(Long customerId, String assetName, OrderStatus status, Instant startDate, Instant endDate);
}
