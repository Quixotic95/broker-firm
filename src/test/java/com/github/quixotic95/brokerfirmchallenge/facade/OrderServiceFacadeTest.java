package com.github.quixotic95.brokerfirmchallenge.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderCreateRequest;
import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderFilter;
import com.github.quixotic95.brokerfirmchallenge.dto.response.OrderDto;
import com.github.quixotic95.brokerfirmchallenge.mapper.OrderMapper;
import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import com.github.quixotic95.brokerfirmchallenge.security.SecurityUtil;
import com.github.quixotic95.brokerfirmchallenge.service.CustomerService;
import com.github.quixotic95.brokerfirmchallenge.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderServiceFacadeTest {

    private OrderService orderService;
    private OrderMapper orderMapper;
    private CustomerService customerService;
    private OrderServiceFacade orderServiceFacade;

    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        orderMapper = mock(OrderMapper.class);
        customerService = mock(CustomerService.class);
        orderServiceFacade = new OrderServiceFacade(orderService, orderMapper, customerService);

        order = Order.builder()
                .id(1L)
                .customerId(1L)
                .assetName("AAPL")
                .orderSide(null)
                .price(BigDecimal.TEN)
                .size(10)
                .status(null)
                .createDate(Instant.now())
                .build();

        orderDto = new OrderDto(1L, 1L, "AAPL", null, 10, BigDecimal.TEN, null, Instant.now());
    }

    @Test
    void createOrder_shouldReturnDto() {
        OrderCreateRequest request = new OrderCreateRequest(1L, "AAPL", null, 10, BigDecimal.TEN);
        when(orderService.createOrder(request)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        OrderDto result = orderServiceFacade.createOrder(request);

        assertThat(result).isEqualTo(orderDto);
    }

    @Test
    void getAllOrders_shouldReturnAll_forAdmin() {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::isAdmin)
                    .thenReturn(true);
            when(orderService.listAll()).thenReturn(List.of(order));
            when(orderMapper.toDtoList(List.of(order))).thenReturn(List.of(orderDto));

            List<OrderDto> result = orderServiceFacade.getAllOrders();

            assertThat(result).containsExactly(orderDto);
        }
    }

    @Test
    void getAllOrders_shouldReturnOwnOrders_forCustomer() {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::isAdmin)
                    .thenReturn(false);
            util.when(SecurityUtil::getCustomerId)
                    .thenReturn(1L);

            when(orderService.listOrders(any())).thenReturn(List.of(order));
            when(orderMapper.toDtoList(List.of(order))).thenReturn(List.of(orderDto));

            List<OrderDto> result = orderServiceFacade.getAllOrders();

            assertThat(result).containsExactly(orderDto);
        }
    }

    @Test
    void getOrdersForCurrentUser_shouldUseResolvedCustomerId_forAdmin() {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::isAdmin)
                    .thenReturn(true);

            Customer mockCustomer = Customer.builder()
                    .id(1L)
                    .username("john")
                    .build();
            when(customerService.findByUsername("john")).thenReturn(mockCustomer);
            when(orderService.listOrders(any())).thenReturn(List.of(order));
            when(orderMapper.toDtoList(List.of(order))).thenReturn(List.of(orderDto));

            OrderFilter filter = new OrderFilter(null, "john", null, null, null);
            List<OrderDto> result = orderServiceFacade.getOrdersForCurrentUser(filter);

            assertThat(result).containsExactly(orderDto);
        }
    }

    @Test
    void getOrdersForCurrentUser_shouldUseCurrentCustomerId_forCustomer() {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(SecurityUtil::isAdmin)
                    .thenReturn(false);
            util.when(SecurityUtil::getCustomerId)
                    .thenReturn(1L);

            OrderFilter filter = new OrderFilter(null, null, null, null, null);

            when(orderService.listOrders(any())).thenReturn(List.of(order));
            when(orderMapper.toDtoList(List.of(order))).thenReturn(List.of(orderDto));

            List<OrderDto> result = orderServiceFacade.getOrdersForCurrentUser(filter);

            assertThat(result).containsExactly(orderDto);
        }
    }
}
