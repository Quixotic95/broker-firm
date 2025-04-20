package com.github.quixotic95.brokerfirmchallenge.service.impl;

import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderFilter;
import com.github.quixotic95.brokerfirmchallenge.enums.OrderSide;
import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;
import com.github.quixotic95.brokerfirmchallenge.exception.InvalidException;
import com.github.quixotic95.brokerfirmchallenge.exception.NotFoundException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import com.github.quixotic95.brokerfirmchallenge.repository.OrderRepository;
import com.github.quixotic95.brokerfirmchallenge.service.AssetService;
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

    @Override
    @Transactional
    public Order createOrder(Long customerId, String assetName, OrderSide orderSide, Integer size, BigDecimal price) {
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(size));

        if (orderSide == OrderSide.BUY) {
            assetService.decreaseUsableSize(customerId, "TRY", totalAmount);
        } else if (orderSide == OrderSide.SELL) {
            assetService.decreaseUsableSize(customerId, assetName, BigDecimal.valueOf(size));
        } else {
            throw new InvalidException(ErrorCode.INVALID_ORDER);
        }

        Order order = Order.builder()
                .customerId(customerId)
                .assetName(assetName)
                .orderSide(orderSide)
                .price(price)
                .size(size)
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));

        order.cancel();

        if (order.getOrderSide() == OrderSide.BUY) {
            assetService.increaseUsableSize(order.getCustomerId(), "TRY", order.calculateTotalAmount());
        } else if (order.getOrderSide() == OrderSide.SELL) {
            assetService.increaseUsableSize(order.getCustomerId(), order.getAssetName(), BigDecimal.valueOf(order.getSize()));
        }

        orderRepository.save(order);
    }

    @Override
    public List<Order> listOrders(OrderFilter filter) {
        if (filter.status() != null) {
            return orderRepository.findAllByCustomerIdAndStatusAndCreateDateBetween(
                    filter.customerId(),
                    filter.status(),
                    filter.startDate(),
                    filter.endDate()
            );
        }
        return orderRepository.findAllByCustomerIdAndCreateDateBetween(
                filter.customerId(),
                filter.startDate(),
                filter.endDate()
        );
    }
}