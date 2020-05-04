package com.example.reatormongoquickdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
class CoffeeServiceTest {

    @Autowired
    private CoffeeService coffeeService;

    @Test
    void get10Orders() {
        String coffeeId = coffeeService.getAllCoffees().blockFirst().getId();

        StepVerifier.withVirtualTime(() -> coffeeService.getOrders(coffeeId).take(10))
                .thenAwait(Duration.ofHours(2))
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void getAllCoffees() {

    }

    @Test
    void getCoffeeById() {
    }

    @Test
    void getOrders() {
    }
}