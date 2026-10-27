package se.iths.christoffer.orderservicegroup2.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Begäran om att skapa en order.
 *
 * @param items produkter och antal som ingår i ordern
 */
public record CreateOrderRequest(
        @NotNull(message = "Items can not be null")
        @NotEmpty(message = "Items can not be empty")
        List<CreateOrderItemRequest> items
) {
}
