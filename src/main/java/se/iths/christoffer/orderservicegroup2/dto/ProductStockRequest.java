package se.iths.christoffer.orderservicegroup2.dto;

/**
 * Produkt-ID och antal som skickas till Product Service vid lagerändring.
 *
 * @param id       produktens ID
 * @param quantity antal produkter
 */
public record ProductStockRequest(
        Long id,
        int quantity
) {
}
