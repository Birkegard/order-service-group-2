package se.iths.christoffer.orderservicegroup2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Konfigurerar autentisering, åtkomstregler och CORS för HTTP-API:et.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Konfigurerar säkerhetsfilter, rollbaserad åtkomst och JWT-autentisering.
     *
     * @param http                       Spring Security-konfigurationen
     * @param jwtAuthenticationConverter konverterare för JWT-roller
     * @return säkerhetsfilterkedjan för HTTP-anrop
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/order/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

        return http.build();
    }

    /**
     * Skapar JWT-avkodaren som hämtar publika nycklar från autentiseringstjänsten.
     *
     * @param authServerUrl autentiseringstjänstens bas-URL
     * @return JWT-avkodare konfigurerad med tjänstens JWKS-endpoint
     */
    @Bean
    public JwtDecoder jwtDecoder(@Value("${auth-service.base-url}") String authServerUrl) {
        return NimbusJwtDecoder
                .withJwkSetUri(authServerUrl + "/auth/jwks")
                .build();

//        jwtDecoder.setJwtValidator(
//                JwtValidators.createDefaultWithIssuer(authServerUrl));
//
//        return jwtDecoder;
    }

    /**
     * Skapar en konverterare som mappar JWT:ns roller till Spring Security-behörigheter.
     *
     * @return konverterare som används vid validering av inkommande token
     */
    @Bean
    public JwtAuthenticationConverter converter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");

            if (roles == null) {
                return List.of();
            }

            return roles.stream()
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
        });

        return converter;
    }

    /**
     * Konfigurerar vilka ursprung, HTTP-metoder och headers som tillåts via CORS.
     *
     * @return källa med tillåtna ursprung, metoder och headers
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
