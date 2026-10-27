package se.iths.christoffer.orderservicegroup2.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import se.iths.christoffer.orderservicegroup2.config.RabbitConfig;
import se.iths.christoffer.orderservicegroup2.dto.OrderResponse;

/**
 * Publicerar orderbekräftelser till RabbitMQ.
 */
@Component
@RequiredArgsConstructor
public class OrderPublisher {
    private final RabbitTemplate rabbitTemplate;

    /**
     * Skickar en orderbekräftelse till orderkön.
     *
     * @param response orderinformationen som ska publiceras
     */
    public void sendOrderConfirmation(OrderResponse response) {
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE, response);
        System.out.println("Order confirmation sent: " + response.id());
    }
}
