package se.iths.christoffer.orderservicegroup2.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import se.iths.christoffer.orderservicegroup2.dto.ProductInfo;
import se.iths.christoffer.orderservicegroup2.dto.ProductStockRequest;

import java.util.List;

/**
 * Anropar Product Service för produkt- och lagerrelaterade operationer.
 */
@Component
@RequiredArgsConstructor
public class ProductClient {
    private final RestClient restClient;

    /**
     * Begär att Product Service minskar lagret för de angivna produkterna.
     *
     * @param items       produkt-ID:n och antal att reservera
     * @param bearerToken autentiseringstoken som skickas vidare i anropet
     * @return produktinformationen som Product Service returnerar
     */
    public List<ProductInfo> decreaseStock(List<ProductStockRequest> items, String bearerToken) {
        return restClient.post()
                .uri("/api/products/decrease-stock")
                .header("Authorization", bearerToken)
                .body(items)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}