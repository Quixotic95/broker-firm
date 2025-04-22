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
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));
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
    public void matchOrders(Long buyOrderId, Long sellOrderId) {
        Order buyOrder = orderRepository.findByIdWithLock(buyOrderId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));
        Order sellOrder = orderRepository.findByIdWithLock(sellOrderId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));


        if (buyOrder.getOrderSide() != OrderSide.BUY || sellOrder.getOrderSide() != OrderSide.SELL) throw new InvalidException(ErrorCode.INVALID_ORDER);

        if (!buyOrder.getAssetName()
                .equals(sellOrder.getAssetName())) throw new InvalidException(ErrorCode.INVALID_ORDER);

        if (buyOrder.getPrice()
                .compareTo(sellOrder.getPrice()) < 0) throw new InvalidException(ErrorCode.INVALID_ORDER);

        if (buyOrder.getCustomerId()
                .equals(sellOrder.getCustomerId())) throw new InvalidException(ErrorCode.INVALID_ORDER);

        if (buyOrder.getStatus() != OrderStatus.PENDING || sellOrder.getStatus() != OrderStatus.PENDING)
            throw new InvalidException(ErrorCode.ORDER_NOT_MATCHABLE);

        int matchedSize = Math.min(buyOrder.getSize(), sellOrder.getSize());
        BigDecimal price = sellOrder.getPrice();
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(matchedSize));

        assetService.increaseSize(sellOrder.getCustomerId(), "TRY", totalAmount);
        assetService.increaseSize(buyOrder.getCustomerId(), buyOrder.getAssetName(), BigDecimal.valueOf(matchedSize));

        if (matchedSize == buyOrder.getSize()) {
            buyOrder.match();
        } else {
            buyOrder.reduceSize(matchedSize);
        }

        if (matchedSize == sellOrder.getSize()) {
            sellOrder.match();
        } else {
            sellOrder.reduceSize(matchedSize);
        }

        orderRepository.saveAll(List.of(buyOrder, sellOrder));
    }

    @Override
    public List<Order> listOrders(OrderFilter filter) {
        Long customerId = filter.customerId();

        if (customerId == null && filter.username() != null) {
            customerId = customerService.findByUsername(filter.username())
                    .getId();
        }

        return orderRepository.findAllByFilters(customerId, filter.assetName(), filter.status(), filter.startDate(), filter.endDate());
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