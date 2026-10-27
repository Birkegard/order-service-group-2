package se.iths.christoffer.orderservicegroup2.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Orderinformation som returneras av Order Service.
 *
 * @param id           orderns ID
 * @param customerName kundens identifierare
 * @param items        produktinformationen i ordern
 * @param totalPrice   orderns totalpris
 */
public record OrderResponse(
        Long id,
        String customerName,
        List<ProductInfo> items,
        BigDecimal totalPrice
) {
}
