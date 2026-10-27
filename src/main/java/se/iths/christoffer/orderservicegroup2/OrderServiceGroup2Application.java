package se.iths.christoffer.orderservicegroup2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Startpunkt för Order Service och Spring Boot-applikationen.
 */
@SpringBootApplication
public class OrderServiceGroup2Application {
    /**
     * Startar Order Service.
     *
     * @param args kommandoradsargument som skickas till applikationen
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceGroup2Application.class, args);
    }

}
