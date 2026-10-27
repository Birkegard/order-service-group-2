package se.iths.christoffer.orderservicegroup2.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * En produkt och det antal som ska ingå i en order.
 *
 * @param id       produktens ID
 * @param quantity antal produkter; måste vara minst 1
 */
public record CreateOrderItemRequest(
        @NotNull
        Long id,
        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity
) {

}