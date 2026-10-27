package se.iths.christoffer.orderservicegroup2.dto;

import java.math.BigDecimal;

/**
 * Bekräftelse om en betalning från betalningstjänsten.
 *
 * @param paymentId             betalningens ID
 * @param orderId               ID för ordern som betalningen gäller
 * @param amount                betalat belopp
 * @param currency              betalningens valuta
 * @param status                betalningens status
 * @param stripePaymentIntentId betalningens Stripe-intent-ID
 */
public record PaymentConfirmationDto(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String currency,
        String status,
        String stripePaymentIntentId
) {
}
