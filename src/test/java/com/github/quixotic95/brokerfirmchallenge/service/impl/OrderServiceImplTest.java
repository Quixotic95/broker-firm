package com.github.quixotic95.brokerfirmchallenge.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderCreateRequest;
import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderFilter;
import com.github.quixotic95.brokerfirmchallenge.enums.OrderSide;
import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;
import com.github.quixotic95.brokerfirmchallenge.exception.InvalidException;
import com.github.quixotic95.brokerfirmchallenge.exception.NotFoundException;
import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import com.github.quixotic95.brokerfirmchallenge.repository.OrderRepository;
import com.github.quixotic95.brokerfirmchallenge.service.AssetService;
import com.github.quixotic95.brokerfirmchallenge.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AssetService assetService;
    @Mock
    private CustomerService customerService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(1L)
                .customerId(1L)
                .assetName("AAPL")
                .orderSide(OrderSide.BUY)
                .size(10)
                .price(new BigDecimal("100"))
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();
    }

    @Test
    void createOrder_shouldReturnSavedOrder() {
        OrderCreateRequest request = new OrderCreateRequest(1L, "AAPL", OrderSide.BUY, 10, new BigDecimal("100"));
        when(orderRepository.save(any())).thenReturn(order);

        Order result = orderService.createOrder(request);

        assertThat(result).isNotNull();
        verify(assetService).decreaseUsableSize(1L, "TRY", new BigDecimal("1000"));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void cancelOrder_shouldRestoreAssetsAndUpdateStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(assetService).increaseUsableSize(1L, "TRY", new BigDecimal("1000"));
        verify(orderRepository).save(order);
    }

    @Test
    void matchOrder_shouldMatchAndUpdateAssets() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.matchOrder(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.MATCHED);
        verify(assetService).decreaseUsableSize(1L, "TRY", new BigDecimal("1000"));
        verify(assetService).increaseSize(1L, "AAPL", BigDecimal.valueOf(10));
        verify(orderRepository).save(order);
    }

    @Test
    void getOrderOrThrow_shouldReturnOrder_whenExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderOrThrow(1L);

        assertThat(result).isEqualTo(order);
    }

    @Test
    void getOrderOrThrow_shouldThrow_whenNotExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderOrThrow(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void cancelOrder_shouldThrow_whenOrderNotPending() {
        order.match();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L)).isInstanceOf(InvalidException.class);
    }

    @Test
    void matchOrder_shouldThrow_whenOrderNotPending() {
        order.match();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.matchOrder(1L)).isInstanceOf(InvalidException.class);
    }

    @Test
    void listAll_shouldReturnAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> orders = orderService.listAll();

        assertThat(orders).containsExactly(order);
    }

    @Test
    void listOrders_shouldResolveCustomerId_fromUsername_whenNull() {
        var mockCustomer = Customer.builder()
                .id(1L)
                .username("john")
                .password("hashed")
                .role(com.github.quixotic95.brokerfirmchallenge.enums.CustomerRole.CUSTOMER)
                .build();

        when(customerService.findByUsername("john")).thenReturn(mockCustomer);

        when(orderRepository.findAllByFilters(eq(1L), any(), any(), any())).thenReturn(List.of(order));

        var filter = new OrderFilter(null, "john", null, null, null);
        List<Order> result = orderService.listOrders(filter);

        assertThat(result).containsExactly(order);
    }

    @Test
    void listOrders_shouldUseCustomerId_whenProvided() {
        when(orderRepository.findAllByFilters(eq(1L), any(), any(), any())).thenReturn(List.of(order));

        var filter = new OrderFilter(1L, null, null, null, null);
        List<Order> result = orderService.listOrders(filter);

        assertThat(result).containsExactly(order);
    }
}