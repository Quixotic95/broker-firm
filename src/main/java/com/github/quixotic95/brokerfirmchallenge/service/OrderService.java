package com.github.quixotic95.brokerfirmchallenge.service;

import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderFilter;
import com.github.quixotic95.brokerfirmchallenge.enums.OrderSide;
import com.github.quixotic95.brokerfirmchallenge.model.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    Order createOrder(Long customerId, String assetName, OrderSide side, Integer size, BigDecimal price);

    void cancelOrder(Long orderId);

    List<Order> listOrders(OrderFilter filter);
}