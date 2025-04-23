package com.github.quixotic95.brokerfirmchallenge.facade;

import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderCreateRequest;
import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderFilter;
import com.github.quixotic95.brokerfirmchallenge.dto.response.OrderDto;
import com.github.quixotic95.brokerfirmchallenge.exception.InsufficientException;
import com.github.quixotic95.brokerfirmchallenge.exception.InvalidException;
import com.github.quixotic95.brokerfirmchallenge.exception.NotFoundException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import com.github.quixotic95.brokerfirmchallenge.mapper.OrderMapper;
import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import com.github.quixotic95.brokerfirmchallenge.security.SecurityUtil;
import com.github.quixotic95.brokerfirmchallenge.service.CustomerService;
import com.github.quixotic95.brokerfirmchallenge.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderServiceFacade {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final CustomerService customerService;

    private static OrderFilter getOrderFilter(OrderFilter filter) {
        Long authenticatedCustomerId = SecurityUtil.getCustomerId();
        if (filter.customerId() != null && !filter.customerId()
                .equals(authenticatedCustomerId)) {
            throw new InsufficientException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        return new OrderFilter(authenticatedCustomerId, null, filter.assetName(), filter.status(), filter.startDate(), filter.endDate());
    }

    public OrderDto createOrder(OrderCreateRequest request) {
        Order order = orderService.createOrder(request);
        return orderMapper.toDto(order);
    }

    public List<OrderDto> getAllOrders() {
        List<Order> orders;
        if (SecurityUtil.isAdmin()) {
            orders = orderService.listAll();
        } else {
            Long customerId = SecurityUtil.getCustomerId();
            orders = orderService.listOrders(new OrderFilter(customerId, null, null, null, null, null));
        }
        return orderMapper.toDtoList(orders);
    }

    public List<OrderDto> getOrdersForCurrentUser(OrderFilter filter) {
        List<Order> orders;
        if (SecurityUtil.isAdmin()) {
            Long customerId = filter.customerId();
            if (filter.username() != null) {
                Customer customer = customerService.findByUsername(filter.username());
                if (customer == null) {
                    throw new NotFoundException(ErrorCode.CUSTOMER_NOT_FOUND);
                }
                if (customerId != null && !customer.getId()
                        .equals(customerId)) {
                    throw new InvalidException(ErrorCode.INVALID_CUSTOMER_FILTER);
                }
                customerId = customer.getId();
            }
            if (customerId == null) {
                throw new InvalidException(ErrorCode.INVALID_ORDER);
            }
            OrderFilter resolved = new OrderFilter(customerId, null, filter.assetName(), filter.status(), filter.startDate(), filter.endDate());
            orders = orderService.listOrders(resolved);
        } else {
            OrderFilter securedFilter = getOrderFilter(filter);
            orders = orderService.listOrders(securedFilter);
        }
        return orderMapper.toDtoList(orders);
    }
}