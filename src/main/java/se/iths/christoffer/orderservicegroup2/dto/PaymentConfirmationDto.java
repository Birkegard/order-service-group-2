package se.iths.christoffer.orderservicegroup2.dto;

import java.math.BigDecimal;

public record PaymentConfirmationDto(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String currency,
        String status,
        String stripePaymentIntentId
) {
}
