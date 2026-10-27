package se.iths.christoffer.orderservicegroup2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Konfigurerar HTTP-klienten som används för anrop till Product Service.
 */
@Configuration
public class ClientConfig {
    /**
     * Skapar en {@link RestClient} med Product Services bas-URL.
     *
     * @param baseUrl bas-URL för Product Service
     * @return konfigurerad HTTP-klient
     */
    @Bean
    public RestClient productRestClient(@Value("${product-service.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
