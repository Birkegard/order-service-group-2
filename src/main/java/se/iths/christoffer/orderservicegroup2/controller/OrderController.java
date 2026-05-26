package se.iths.christoffer.orderservicegroup2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.iths.christoffer.orderservicegroup2.dto.CreateOrderRequest;
import se.iths.christoffer.orderservicegroup2.service.OrderService;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid @RequestBody CreateOrderRequest orderRequest, @AuthenticationPrincipal Jwt jwt) {
        String bearerToken = "Bearer: " + jwt.getTokenValue();
        orderService.createOrder(orderRequest, jwt.getSubject(), bearerToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
