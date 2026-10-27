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

/**
 * Hanterar orderflödet, inklusive lagerkontroll, lagring och betalningsstatus.
 */
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final ProductClient client;
    private final OrderPublisher publisher;

    /**
     * Skapar och sparar en order efter att produktlagret har minskats av Product Service.
     *
     * @param orderRequest produkterna och antalen som ska beställas
     * @param customerName kundens identifierare
     * @param bearerToken  autentiseringstoken som vidarebefordras till Product Service
     * @return den skapade ordern med produktinformation och totalpris
     */
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

    /**
     * Markerar en väntande order som betald och skickar en orderbekräftelse.
     *
     * @param orderId ID för ordern som ska markeras som betald
     * @throws IllegalArgumentException om ordern inte finns
     * @throws IllegalStateException    om ordern inte har statusen {@code PENDING}
     */
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
        publisher.sendOrderConfirmation(objectMapper.toOrderResponse(order));
    }

    /**
     * Hämtar betalningsuppgifter för en order om den tillhör den angivna användaren
     * och fortfarande väntar på betalning.
     *
     * @param id      orderns ID
     * @param subject användaridentifieraren från JWT:ns {@code sub}-claim
     * @return orderns ID, belopp, valuta och status
     * @throws IllegalArgumentException om ordern inte finns
     * @throws AccessDeniedException    om ordern tillhör en annan användare
     * @throws IllegalStateException    om ordern inte har statusen {@code PENDING}
     */
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