package se.iths.christoffer.orderservicegroup2.dto;

import java.math.BigDecimal;

public record ProductInfo(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stock
) {
}
