package se.iths.christoffer.orderservicegroup2.dto;

import se.iths.christoffer.orderservicegroup2.model.OrderStatus;

import java.math.BigDecimal;

public record PaymentOrderDetailsDto(
        Long id,
        BigDecimal amount,
        String currency,
        OrderStatus status
) {
}
