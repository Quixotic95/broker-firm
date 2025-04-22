package com.github.quixotic95.brokerfirmchallenge.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderCreateRequest;
import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderFilter;
import com.github.quixotic95.brokerfirmchallenge.enums.CustomerRole;
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

    private Order buyOrder;
    private Order sellOrder;

    @BeforeEach
    void setUp() {
        buyOrder = Order.builder()
                .id(1L)
                .customerId(1L)
                .assetName("AAPL")
                .orderSide(OrderSide.BUY)
                .size(10)
                .price(new BigDecimal("100"))
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();

        sellOrder = Order.builder()
                .id(2L)
                .customerId(2L)
                .assetName("AAPL")
                .orderSide(OrderSide.SELL)
                .size(10)
                .price(new BigDecimal("80"))
                .status(OrderStatus.PENDING)
                .createDate(Instant.now())
                .build();
    }

    @Test
    void listOrders_shouldResolveCustomerId_fromUsername_whenNull() {
        Customer mockCustomer = Customer.builder()
                .id(1L)
                .username("john")
                .password("hashed")
                .role(CustomerRole.CUSTOMER)
                .build();

        when(customerService.findByUsername("john")).thenReturn(mockCustomer);
        when(orderRepository.findAllByFilters(eq(1L), eq("AAPL"), any(), any(), any())).thenReturn(List.of(buyOrder));

        OrderFilter filter = new OrderFilter(null, "john", "AAPL", null, null, null);
        List<Order> result = orderService.listOrders(filter);

        assertThat(result).containsExactly(buyOrder);
    }

    @Test
    void listOrders_shouldUseCustomerId_whenProvided() {
        when(orderRepository.findAllByFilters(eq(1L), eq("AAPL"), any(), any(), any())).thenReturn(List.of(buyOrder));

        OrderFilter filter = new OrderFilter(1L, null, "AAPL", null, null, null);
        List<Order> result = orderService.listOrders(filter);

        assertThat(result).containsExactly(buyOrder);
    }

    @Test
    void listOrders_shouldFilterByAssetNameOnly() {
        OrderFilter filter = new OrderFilter(null, null, "AAPL", null, null, null);
        when(orderRepository.findAllByFilters(null, "AAPL", null, null, null)).thenReturn(List.of(buyOrder));

        List<Order> result = orderService.listOrders(filter);

        assertThat(result).containsExactly(buyOrder);
    }

    @Test
    void createOrder_shouldReturnSavedOrder() {
        OrderCreateRequest request = new OrderCreateRequest(1L, "AAPL", OrderSide.BUY, 10, new BigDecimal("100"));
        when(orderRepository.save(any())).thenReturn(buyOrder);

        Order result = orderService.createOrder(request);

        assertThat(result).isNotNull();
        verify(assetService).decreaseUsableSize(1L, "TRY", new BigDecimal("1000"));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void cancelOrder_shouldRestoreAssetsAndUpdateStatus() {
        when(orderRepository.findByIdWithLock(1L)).thenReturn(Optional.of(buyOrder));

        orderService.cancelOrder(1L);

        assertThat(buyOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(assetService).increaseUsableSize(1L, "TRY", new BigDecimal("1000"));
        verify(orderRepository).save(buyOrder);
    }

    @Test
    void cancelOrder_shouldThrow_whenNotFound() {
        when(orderRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getOrderOrThrow_shouldReturnOrder_whenExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(buyOrder));

        Order result = orderService.getOrderOrThrow(1L);

        assertThat(result).isEqualTo(buyOrder);
    }

    @Test
    void getOrderOrThrow_shouldThrow_whenNotExists() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderOrThrow(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void listAll_shouldReturnAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(buyOrder));

        List<Order> orders = orderService.listAll();

        assertThat(orders).containsExactly(buyOrder);
    }

    @Test
    void matchOrders_shouldFullyMatchOrders() {
        when(orderRepository.findByIdWithLock(1L)).thenReturn(Optional.of(buyOrder));
        when(orderRepository.findByIdWithLock(2L)).thenReturn(Optional.of(sellOrder));

        orderService.matchOrders(1L, 2L);

        assertThat(buyOrder.getStatus()).isEqualTo(OrderStatus.MATCHED);
        assertThat(sellOrder.getStatus()).isEqualTo(OrderStatus.MATCHED);

        verify(assetService).increaseSize(2L, "TRY", new BigDecimal("800"));
        verify(assetService).increaseSize(1L, "AAPL", BigDecimal.valueOf(10));
        verify(orderRepository).saveAll(List.of(buyOrder, sellOrder));
    }

    @Test
    void matchOrders_shouldPartiallyMatchOrders() {
        buyOrder = buyOrder.toBuilder()
                .size(50)
                .build();
        sellOrder = sellOrder.toBuilder()
                .size(30)
                .build();

        when(orderRepository.findByIdWithLock(1L)).thenReturn(Optional.of(buyOrder));
        when(orderRepository.findByIdWithLock(2L)).thenReturn(Optional.of(sellOrder));

        orderService.matchOrders(1L, 2L);

        assertThat(buyOrder.getSize()).isEqualTo(20);
        assertThat(buyOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(sellOrder.getStatus()).isEqualTo(OrderStatus.MATCHED);
    }

    @Test
    void matchOrders_shouldThrow_whenAssetNamesMismatch() {
        sellOrder = sellOrder.toBuilder()
                .assetName("GOOG")
                .build();
        when(orderRepository.findByIdWithLock(1L)).thenReturn(Optional.of(buyOrder));
        when(orderRepository.findByIdWithLock(2L)).thenReturn(Optional.of(sellOrder));

        assertThatThrownBy(() -> orderService.matchOrders(1L, 2L)).isInstanceOf(InvalidException.class);
    }

    @Test
    void matchOrders_shouldThrow_whenSameCustomer() {
        sellOrder = sellOrder.toBuilder()
                .customerId(1L)
                .build();
        when(orderRepository.findByIdWithLock(1L)).thenReturn(Optional.of(buyOrder));
        when(orderRepository.findByIdWithLock(2L)).thenReturn(Optional.of(sellOrder));

        assertThatThrownBy(() -> orderService.matchOrders(1L, 2L)).isInstanceOf(InvalidException.class);
    }

    @Test
    void matchOrders_shouldThrow_whenPriceMismatch() {
        sellOrder = sellOrder.toBuilder()
                .price(new BigDecimal("120"))
                .build();
        when(orderRepository.findByIdWithLock(1L)).thenReturn(Optional.of(buyOrder));
        when(orderRepository.findByIdWithLock(2L)).thenReturn(Optional.of(sellOrder));

        assertThatThrownBy(() -> orderService.matchOrders(1L, 2L)).isInstanceOf(InvalidException.class);
    }

    @Test
    void matchOrders_shouldThrow_whenOneOrderNotPending() {
        buyOrder.match();
        when(orderRepository.findByIdWithLock(1L)).thenReturn(Optional.of(buyOrder));
        when(orderRepository.findByIdWithLock(2L)).thenReturn(Optional.of(sellOrder));

        assertThatThrownBy(() -> orderService.matchOrders(1L, 2L)).isInstanceOf(InvalidException.class);
    }
}
