package se.iths.christoffer.orderservicegroup2.dto;

public record CreateOrderItemRequest(
        Long id,
        int quantity
) {

}