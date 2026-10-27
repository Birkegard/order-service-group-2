package se.iths.christoffer.orderservicegroup2.dto;

import se.iths.christoffer.orderservicegroup2.model.OrderStatus;

import java.math.BigDecimal;

/**
 * Betalningsuppgifter för en väntande order.
 *
 * @param id       orderns ID
 * @param amount   beloppet som ska betalas
 * @param currency valutan för beloppet
 * @param status   orderns aktuella status
 */
public record PaymentOrderDetailsDto(
        Long id,
        BigDecimal amount,
        String currency,
        OrderStatus status
) {
}
