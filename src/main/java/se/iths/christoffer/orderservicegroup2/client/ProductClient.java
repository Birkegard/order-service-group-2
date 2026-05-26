package se.iths.christoffer.orderservicegroup2.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import se.iths.christoffer.orderservicegroup2.dto.ProductInfo;
import se.iths.christoffer.orderservicegroup2.dto.ProductStockRequest;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductClient {
    private final RestClient restClient;

    public List<ProductInfo> decreaseStock(List<ProductStockRequest> items, String bearerToken) {
        return restClient.post()
                .uri("/products/stock/decrease")
                .header("Authorization ", bearerToken)
                .body(items)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}