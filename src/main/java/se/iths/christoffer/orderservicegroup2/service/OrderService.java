package se.iths.christoffer.orderservicegroup2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.iths.christoffer.orderservicegroup2.client.ProductClient;
import se.iths.christoffer.orderservicegroup2.dto.*;
import se.iths.christoffer.orderservicegroup2.mapper.ObjectMapper;
import se.iths.christoffer.orderservicegroup2.model.Order;
import se.iths.christoffer.orderservicegroup2.model.OrderItem;
import se.iths.christoffer.orderservicegroup2.model.OrderStatus;
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


    public OrderResponse createOrder(CreateOrderRequest orderRequest, String customerName, String bearerToken) {
        List<ProductStockRequest> requestList = orderRequest.items()
                .stream()
                .map(item -> new ProductStockRequest(item.id(), item.quantity()))
                .toList();

        List<ProductInfo> productInfo = client.decreaseStock(requestList, bearerToken);

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
        order.setStatus(OrderStatus.PENDING);

        orderRepository.save(order);

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                productInfo,
                order.getTotalPrice()
        );
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

    @Transactional
    public void markOrderAsPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order: " + orderId + " not found"));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            return;
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot mark order " + orderId + " as paid from status " + order.getStatus());
        }

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }

    public PaymentOrderDetailsDto getOrderForPayment(Long id, String subject) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order: " + id + " not found"));

        if (!subject.equals(order.getCustomerName())) {
            throw new AccessDeniedException("User does not own this order");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not payable");
        }

        return new PaymentOrderDetailsDto(
                order.getId(),
                order.getTotalPrice(),
                "SEK", // Assuming a default currency
                order.getStatus()
        );
    }
}