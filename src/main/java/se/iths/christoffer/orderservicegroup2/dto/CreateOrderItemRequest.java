package se.iths.christoffer.orderservicegroup2.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderItemRequest(
        @NotNull
        Long id,
        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity
) {

}