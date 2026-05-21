package se.iths.christoffer.orderservicegroup2.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        List<ProductInfo> items,
        BigDecimal totalPrice
) {
}
