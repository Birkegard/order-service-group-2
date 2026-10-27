package se.iths.christoffer.orderservicegroup2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import se.iths.christoffer.orderservicegroup2.dto.OrderResponse;
import se.iths.christoffer.orderservicegroup2.model.Order;

/**
 * Mappar mellan orderns datamodell och objekt som används av API:et.
 */
@Mapper(componentModel = "spring")
public interface ObjectMapper {
    /**
     * Skapar ett API-svar från en order.
     *
     * @param order ordern som ska mappas
     * @return ordern representerad som ett API-svar
     */
    @Mapping(source = "orderItems", target = "items")
    OrderResponse toOrderResponse(Order order);
}
