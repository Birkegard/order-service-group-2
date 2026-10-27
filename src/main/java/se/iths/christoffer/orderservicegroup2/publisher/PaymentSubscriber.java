package se.iths.christoffer.orderservicegroup2.publisher;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import se.iths.christoffer.orderservicegroup2.config.RabbitConfig;
import se.iths.christoffer.orderservicegroup2.dto.PaymentConfirmationDto;
import se.iths.christoffer.orderservicegroup2.service.OrderService;

/**
 * Tar emot betalningsbekräftelser från RabbitMQ och uppdaterar orderstatus.
 */
@Component
public class PaymentSubscriber {

    private final OrderService orderService;

    /**
     * Skapar en prenumerant som hanterar betalningsbekräftelser.
     *
     * @param orderService tjänsten som uppdaterar orderstatus
     */
    public PaymentSubscriber(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Hanterar en betalningsbekräftelse. Bekräftelser med annan status än
     * {@code COMPLETED} ignoreras.
     *
     * @param dto betalningsbekräftelsen från betalningstjänsten
     */
    @RabbitListener(queues = RabbitConfig.PAYMENT_QUEUE)
    public void handlePaymentConfirmation(PaymentConfirmationDto dto) {
        if (!"COMPLETED".equals(dto.status())) {
            return;
        }
        orderService.markOrderAsPaid(dto.orderId());
    }
}
