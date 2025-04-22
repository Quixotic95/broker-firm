package com.github.quixotic95.brokerfirmchallenge.facade;

import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderCreateRequest;
import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderFilter;
import com.github.quixotic95.brokerfirmchallenge.dto.response.OrderDto;
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
            orders = orderService.listOrders(new OrderFilter(customerId, null, null, null, null));
        }
        return orderMapper.toDtoList(orders);
    }

    public List<OrderDto> getOrdersForCurrentUser(OrderFilter filter) {
        List<Order> orders;
        if (SecurityUtil.isAdmin()) {
            Long customerId = filter.customerId();
            if (customerId == null && filter.username() != null) {
                Customer customer = customerService.findByUsername(filter.username());
                customerId = customer.getId();
            }
            OrderFilter resolved = new OrderFilter(customerId, null, filter.status(), filter.startDate(), filter.endDate());
            orders = orderService.listOrders(resolved);
        } else {
            Long customerId = SecurityUtil.getCustomerId();
            OrderFilter securedFilter = new OrderFilter(customerId, null, filter.status(), filter.startDate(), filter.endDate());
            orders = orderService.listOrders(securedFilter);
        }
        return orderMapper.toDtoList(orders);
    }


}
