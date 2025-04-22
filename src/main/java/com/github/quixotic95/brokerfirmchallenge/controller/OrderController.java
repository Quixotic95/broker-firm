package com.github.quixotic95.brokerfirmchallenge.controller;

import com.github.quixotic95.brokerfirmchallenge.aspect.AuthorizeEndpoint;
import com.github.quixotic95.brokerfirmchallenge.dto.request.MatchOrdersRequest;
import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderCreateRequest;
import com.github.quixotic95.brokerfirmchallenge.dto.request.OrderFilter;
import com.github.quixotic95.brokerfirmchallenge.dto.response.OrderDto;
import com.github.quixotic95.brokerfirmchallenge.facade.OrderServiceFacade;
import com.github.quixotic95.brokerfirmchallenge.mapper.OrderMapper;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import com.github.quixotic95.brokerfirmchallenge.security.SecurityUtil;
import com.github.quixotic95.brokerfirmchallenge.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Customer order operations including create, cancel, and list")
public class OrderController {

    private final OrderService orderService;
    private final OrderServiceFacade orderServiceFacade;
    private final OrderMapper orderMapper;

    @Operation(summary = "Create Order", description = "Create a new BUY or SELL order for a specific asset.")
    @ApiResponses(
            {@ApiResponse(responseCode = "201", description = "Order created successfully"),
                    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)})
    @PostMapping("/create")
    @AuthorizeEndpoint(customerAccessible = true)
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Long customerId = SecurityUtil.getCustomerId();
        OrderCreateRequest securedRequest = new OrderCreateRequest(customerId, request.assetName(), request.side(), request.size(), request.price());
        Order order = orderService.createOrder(securedRequest);
        return ResponseEntity.created(URI.create("/api/orders/" + order.getId()))
                .body(orderMapper.toDto(order));
    }

    @Operation(summary = "Cancel Order", description = "Cancel a PENDING order if it has not been matched.")
    @ApiResponses(
            {@ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized or forbidden")})
    @DeleteMapping("/{orderId}")
    @AuthorizeEndpoint(customerAccessible = true, checkOwnership = true)
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "List Orders", description = "List all orders for the authenticated customer, optionally filtered by status and date range.")
    @ApiResponses(
            {@ApiResponse(responseCode = "200", description = "Orders retrieved successfully")})
    @PostMapping("/list")
    @AuthorizeEndpoint(customerAccessible = true)
    public ResponseEntity<List<OrderDto>> listOrders(@Valid @RequestBody OrderFilter filter) {
        Long customerId = SecurityUtil.getCustomerId();
        OrderFilter securedFilter = new OrderFilter(customerId, filter.username(), filter.status(), filter.startDate(), filter.endDate());
        List<Order> orders = orderService.listOrders(securedFilter);
        return ResponseEntity.ok(orderMapper.toDtoList(orders));
    }

    @PostMapping("/match")
    @AuthorizeEndpoint(customerAccessible = false)
    @Operation(summary = "Match Buy and Sell Orders", description = "Admin manually matches a BUY and SELL order.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Orders matched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid match request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    public ResponseEntity<Void> matchOrders(@Valid @RequestBody MatchOrdersRequest request) {
        orderService.matchOrders(request.buyOrderId(), request.sellOrderId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List All Orders", description = "Admins can view all orders, customers will only see their own.")
    @ApiResponses(
            {@ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied")})
    @GetMapping("/all")
    @AuthorizeEndpoint(customerAccessible = true)
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderServiceFacade.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
