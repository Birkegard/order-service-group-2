package se.iths.christoffer.orderservicegroup2.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import se.iths.christoffer.orderservicegroup2.dto.CreateOrderRequest;
import se.iths.christoffer.orderservicegroup2.dto.OrderResponse;
import se.iths.christoffer.orderservicegroup2.dto.PaymentOrderDetailsDto;
import se.iths.christoffer.orderservicegroup2.service.OrderService;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest orderRequest, @AuthenticationPrincipal Jwt jwt) {
        String bearerToken = "Bearer " + jwt.getTokenValue();
        OrderResponse response = orderService.createOrder(orderRequest, jwt.getSubject(), bearerToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentOrderDetailsDto> getOrderForPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(orderService.getOrderForPayment(id, jwt.getSubject()));
    }
}
