package se.iths.christoffer.orderservicegroup2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.iths.christoffer.orderservicegroup2.client.ProductClient;
import se.iths.christoffer.orderservicegroup2.dto.CreateOrderRequest;
import se.iths.christoffer.orderservicegroup2.dto.OrderResponse;
import se.iths.christoffer.orderservicegroup2.dto.ProductInfo;
import se.iths.christoffer.orderservicegroup2.dto.ProductStockRequest;
import se.iths.christoffer.orderservicegroup2.mapper.ObjectMapper;
import se.iths.christoffer.orderservicegroup2.model.Order;
import se.iths.christoffer.orderservicegroup2.model.OrderItem;
import se.iths.christoffer.orderservicegroup2.publisher.OrderPublisher;
import se.iths.christoffer.orderservicegroup2.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final ProductClient client;
    private final OrderPublisher publisher;


    public OrderResponse createOrder(CreateOrderRequest orderRequest, String customerName) {
        List<ProductStockRequest> requestList = orderRequest.items()
                .stream()
                .map(item -> new ProductStockRequest(item.id(), item.quantity()))
                .toList();

        List<ProductInfo> productInfo = client.decreaseStock(requestList, customerName);

        List<OrderItem> orderItemList = new ArrayList<>();

        for (ProductInfo info : productInfo) {
            OrderItem orderItem = new OrderItem();
            orderItem.setName(info.name());
            orderItem.setPrice(info.price());
            orderItem.setQuantity(info.quantity());
            orderItemList.add(orderItem);
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setOrderItems(orderItemList);
        order.setOrderDate(LocalDate.now());
        order.setTotalPrice(totalPrice(orderItemList));

        orderRepository.save(order);

        OrderResponse response = objectMapper.toOrderResponse(order);
        publisher.sendOrderConfirmation(response);

        return response;
    }

    private BigDecimal totalPrice(List<OrderItem> itemList) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem orderItem : itemList) {
            totalPrice = totalPrice.add(orderItem.getPrice()
                    .multiply(new BigDecimal(orderItem.getQuantity())));

        }
        return totalPrice;
    }
}
