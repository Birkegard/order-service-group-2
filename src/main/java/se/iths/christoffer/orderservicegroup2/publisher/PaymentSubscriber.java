package se.iths.christoffer.orderservicegroup2.publisher;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import se.iths.christoffer.orderservicegroup2.dto.PaymentConfirmationDto;
import se.iths.christoffer.orderservicegroup2.service.OrderService;

@Component
public class PaymentSubscriber {

    private final OrderService orderService;

    public PaymentSubscriber(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = "payment-queue")
    public void handlePaymentConfirmation(PaymentConfirmationDto dto) {
        if (!"COMPLETED".equals(dto.status())) {
            return;
        }
        orderService.markOrderAsPaid(dto.orderId());
    }
}
