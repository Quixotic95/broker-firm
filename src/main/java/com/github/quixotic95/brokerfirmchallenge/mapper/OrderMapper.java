package com.github.quixotic95.brokerfirmchallenge.mapper;

import com.github.quixotic95.brokerfirmchallenge.dto.response.OrderDto;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderDto toDto(Order order) {
        return new OrderDto(order.getId(), order.getCustomerId(), order.getAssetName(), order.getOrderSide(), order.getSize(), order.getPrice(), order.getStatus(), order.getCreateDate());
    }

    public List<OrderDto> toDtoList(List<Order> orders) {
        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}


