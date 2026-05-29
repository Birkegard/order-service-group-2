package se.iths.christoffer.orderservicegroup2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import se.iths.christoffer.orderservicegroup2.dto.OrderResponse;
import se.iths.christoffer.orderservicegroup2.model.Order;

@Mapper(componentModel = "spring")
public interface ObjectMapper {
    @Mapping(source = "orderItems", target = "items")
    OrderResponse toOrderResponse(Order order);
}
