package com.github.quixotic95.brokerfirmchallenge.service.impl;

import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderCreateRequest;
import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderFilter;
import com.github.quixotic95.brokerfirmchallenge.enums.OrderSide;
import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;
import com.github.quixotic95.brokerfirmchallenge.exception.InvalidException;
import com.github.quixotic95.brokerfirmchallenge.exception.NotFoundException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import com.github.quixotic95.brokerfirmchallenge.repository.OrderRepository;
import com.github.quixotic95.brokerfirmchallenge.service.AssetService;
import com.github.quixotic95.brokerfirmchallenge.service.CustomerService;
import com.github.quixotic95.brokerfirmchallenge.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AssetService assetService;
    private final CustomerService customerService;

    @Override
    @Transactional
    public Order createOrder(OrderCreateRequest request) {
        BigDecimal totalAmount = request.price()
                .multiply(BigDecimal.valueOf(request.size()));

        if (request.side() == OrderSide.BUY) {
            assetService.decreaseUsableSize(request.customerId(), "TRY", totalAmount);
        } else {
            assetService.decreaseUsableSize(request.customerId(), request.assetName(), BigDecimal.valueOf(request.size()));
        }

        Order order = Order.builder()
                .customerId(request.customerId())
                .assetName(request.assetName())
                .orderSide(request.side())
                .price(request.price())
                .size(request.size())
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);
        order.cancel();

        if (order.getOrderSide() == OrderSide.BUY) {
            assetService.increaseUsableSize(order.getCustomerId(), "TRY", order.calculateTotalAmount());
        } else {
            assetService.increaseUsableSize(order.getCustomerId(), order.getAssetName(), BigDecimal.valueOf(order.getSize()));
        }

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void matchOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidException(ErrorCode.ORDER_NOT_MATCHABLE);
        }

        order.match();

        Long customerId = order.getCustomerId();
        String assetName = order.getAssetName();
        BigDecimal totalAmount = order.calculateTotalAmount();

        if (order.getOrderSide() == OrderSide.BUY) {
            assetService.decreaseUsableSize(customerId, "TRY", totalAmount);
            assetService.increaseSize(customerId, assetName, BigDecimal.valueOf(order.getSize()));
        } else {
            assetService.decreaseUsableSize(customerId, assetName, BigDecimal.valueOf(order.getSize()));
            assetService.increaseSize(customerId, "TRY", totalAmount);
        }

        orderRepository.save(order);
    }

    @Override
    public List<Order> listOrders(OrderFilter filter) {
        Long customerId = filter.customerId();

        if (customerId == null && filter.username() != null) {
            customerId = customerService.findByUsername(filter.username())
                    .getId();
        }

        return orderRepository.findAllByFilters(customerId, filter.status(), filter.startDate(), filter.endDate());
    }

    @Override
    public List<Order> listAll() {
        return orderRepository.findAll();
    }

    //public for test purposes
    public Order getOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));
    }
}
