package com.github.quixotic95.brokerfirmchallenge.service;

import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderCreateRequest;
import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderFilter;
import com.github.quixotic95.brokerfirmchallenge.model.Order;

import java.util.List;

public interface OrderService {

    Order createOrder(OrderCreateRequest request);

    void cancelOrder(Long orderId);

    void matchOrders(Long buyOrderId, Long sellOrderId);

    List<Order> listOrders(OrderFilter filter);

    List<Order> listAll();
}