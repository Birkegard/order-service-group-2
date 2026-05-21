package se.iths.christoffer.orderservicegroup2.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPublisher {
    private final RabbitTemplate rabbitTemplate;
//TODO: Skicka till messaging-service
//    public void publish() {
//        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE, );
//    }
}
