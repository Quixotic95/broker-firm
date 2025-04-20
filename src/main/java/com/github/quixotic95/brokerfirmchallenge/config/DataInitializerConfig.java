package com.github.quixotic95.brokerfirmchallenge.config;

import com.github.quixotic95.brokerfirmchallenge.enums.CustomerRole;
import com.github.quixotic95.brokerfirmchallenge.enums.OrderSide;
import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;
import com.github.quixotic95.brokerfirmchallenge.model.Asset;
import com.github.quixotic95.brokerfirmchallenge.model.AssetId;
import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import com.github.quixotic95.brokerfirmchallenge.repository.AssetRepository;
import com.github.quixotic95.brokerfirmchallenge.repository.CustomerRepository;
import com.github.quixotic95.brokerfirmchallenge.repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Configuration
public class DataInitializerConfig {

    @Bean
    public CommandLineRunner initData(OrderRepository orderRepository, AssetRepository assetRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Customer john = Customer.builder()
                    .username("john_doe")
                    .password(passwordEncoder.encode("password1"))
                    .role(CustomerRole.CUSTOMER)
                    .build();

            Customer alice = Customer.builder()
                    .username("alice")
                    .password(passwordEncoder.encode("password2"))
                    .role(CustomerRole.CUSTOMER)
                    .build();

            Customer bob = Customer.builder()
                    .username("bob")
                    .password(passwordEncoder.encode("password3"))
                    .role(CustomerRole.CUSTOMER)
                    .build();

            Customer admin = Customer.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("adminpass"))
                    .role(CustomerRole.ADMIN)
                    .build();

            customerRepository.saveAll(List.of(john, alice, bob, admin));

            assetRepository.saveAll(List.of(
                    // john
                    new Asset(AssetId.of(john.getId(), "TRY"), new BigDecimal("10000"), new BigDecimal("9000")), new Asset(AssetId.of(john.getId(), "AAPL"), new BigDecimal("50"), new BigDecimal("30")),

                    // alice
                    new Asset(AssetId.of(alice.getId(), "TRY"), new BigDecimal("20000"), new BigDecimal("18500")), new Asset(AssetId.of(alice.getId(), "TSLA"), new BigDecimal("100"), new BigDecimal("60")),

                    // bob
                    new Asset(AssetId.of(bob.getId(), "TRY"), new BigDecimal("5000"), new BigDecimal("5000")), new Asset(AssetId.of(bob.getId(), "GOOG"), new BigDecimal("20"), new BigDecimal("20"))));

            orderRepository.saveAll(List.of(
                    // john
                    Order.builder()
                            .customerId(john.getId())
                            .assetName("AAPL")
                            .orderSide(OrderSide.BUY)
                            .size(10)
                            .price(new BigDecimal("100"))
                            .status(OrderStatus.PENDING)
                            .createDate(Instant.now()
                                    .minusSeconds(6000))
                            .build(),

                    Order.builder()
                            .customerId(john.getId())
                            .assetName("AAPL")
                            .orderSide(OrderSide.SELL)
                            .size(20)
                            .price(new BigDecimal("110"))
                            .status(OrderStatus.PENDING)
                            .createDate(Instant.now()
                                    .minusSeconds(5000))
                            .build(),

                    // alice
                    Order.builder()
                            .customerId(alice.getId())
                            .assetName("TSLA")
                            .orderSide(OrderSide.BUY)
                            .size(15)
                            .price(new BigDecimal("100"))
                            .status(OrderStatus.PENDING)
                            .createDate(Instant.now()
                                    .minusSeconds(4000))
                            .build(),

                    Order.builder()
                            .customerId(alice.getId())
                            .assetName("TSLA")
                            .orderSide(OrderSide.SELL)
                            .size(40)
                            .price(new BigDecimal("90"))
                            .status(OrderStatus.PENDING)
                            .createDate(Instant.now()
                                    .minusSeconds(3000))
                            .build()));
        };
    }
}
