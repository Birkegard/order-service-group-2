package se.iths.christoffer.orderservicegroup2.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konstanter och Spring-bönor för RabbitMQ-köer och meddelandehantering.
 */
@Configuration
public class RabbitConfig {
    /**
     * Kö för orderbekräftelser som skickas från Order Service.
     */
    public static final String QUEUE = "order-queue";
    /**
     * Kö där Order Service tar emot betalningsbekräftelser.
     */
    public static final String PAYMENT_QUEUE = "payment-queue";

    /**
     * Skapar den beständiga kön för orderbekräftelser.
     *
     * @return kön {@value #QUEUE}, som överlever en omstart av RabbitMQ
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    /**
     * Skapar den beständiga kön för betalningsbekräftelser.
     *
     * @return kön {@value #PAYMENT_QUEUE}, som överlever en omstart av RabbitMQ
     */
    @Bean
    public Queue paymentqueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }

    /**
     * Skapar JSON-konverteraren som används för RabbitMQ-meddelanden.
     *
     * @return konverterare som serialiserar och deserialiserar meddelanden som JSON
     */
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    /**
     * Skapar en RabbitMQ-klient som använder JSON för meddelanden.
     *
     * @param connectionFactory anslutningsfabriken för RabbitMQ
     * @return konfigurerad RabbitMQ-klient
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
