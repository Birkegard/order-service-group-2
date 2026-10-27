package se.iths.christoffer.orderservicegroup2.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurerar API-informationen och JWT-baserad autentisering i OpenAPI-dokumentationen.
 */
@Configuration
public class OpenAPIConfig {
    /**
     * Skapar OpenAPI-beskrivningen för Order Service.
     *
     * @return OpenAPI-konfiguration med API-information och bearer-autentisering
     */
    @Bean
    public OpenAPI openAPI() {
        String schemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("Simple JWT protected order-service")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
