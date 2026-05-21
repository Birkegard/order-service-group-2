package se.iths.christoffer.orderservicegroup2.mapper;

import org.mapstruct.Mapper;
import se.iths.christoffer.orderservicegroup2.dto.CreateOrderRequest;
import se.iths.christoffer.orderservicegroup2.dto.OrderResponse;
import se.iths.christoffer.orderservicegroup2.model.Order;

@Mapper(componentModel = "spring")
public interface ObjectMapper {
    Order toEntity(CreateOrderRequest dto);

    OrderResponse toOrderResponse(Order order);
}
