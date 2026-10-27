package se.iths.christoffer.orderservicegroup2.dto;

import java.math.BigDecimal;

/**
 * Produktinformation som returneras av Product Service.
 *
 * @param id          produktens ID
 * @param name        produktens namn
 * @param description produktbeskrivning
 * @param price       priset per produkt
 * @param quantity    antal produkter
 */
public record ProductInfo(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int quantity
) {
}