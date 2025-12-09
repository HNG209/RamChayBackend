package iuh.fit.se.configs;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomJwtDecoder decoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth // [Giai đoạn 3]: cho phép đi qua nếu là public
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/orders/*/guest").permitAll() // Cho phép khách vãng lai xem đơn hàng
                        .requestMatchers(
                                "/managers/**",
                                "/auth/login",
                                "/auth/admin-login",
                                "/auth/logout",
                                "/auth/register",
                                "/auth/refresh",
                                "/cart-items/**",
                                "/orders", // Cho phép khách vãng lai tạo đơn hàng
                                "/carts",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html").permitAll() // Không cần điền context path /api
                        .anyRequest().authenticated()
                );

        http.oauth2ResourceServer(oauth2 -> oauth2
                // [Giai đoạn 1]: tìm kiếm token trong cookies hoặc bearer
                .bearerTokenResolver(tokenResolver())

                // [Giai đoạn 2]: parse token
                .jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(decoder)
                        .jwtAuthenticationConverter(converter())
                )
                .authenticationEntryPoint(new AuthEntryPoint())
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Chỉ cho phép port 3000 (Next.js)
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // Các method được phép
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Các header được phép
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));

        // Cho phép gửi Cookie/Credentials
        configuration.setAllowCredentials(true);

        // Thời gian cache preflight request (tùy chọn)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BearerTokenResolver tokenResolver() {
        DefaultBearerTokenResolver defaultResolver = new DefaultBearerTokenResolver();

        return request -> {
            String requestPath = request.getRequestURI();
            if (requestPath.contains("/auth/refresh") || requestPath.contains("/auth/logout")) {
                return null;
            }

            // 1. Thử tìm trong Header trước (cách cũ)
            String token = defaultResolver.resolve(request);
            if (token != null) {
                return token;
            }

            // 2. Nếu Header không có, tìm trong Cookie "accessToken"
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("accessToken".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }

            // 3. Không thấy đâu cả
            return null;
        };
    }

    @Bean
    JwtAuthenticationConverter converter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // Lấy roles
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                roles.forEach(r -> authorities.add(new SimpleGrantedAuthority(r)));
            }

            // Lấy permissions
            List<String> permissions = jwt.getClaimAsStringList("permissions");
            if (permissions != null) {
                permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
            }

            return authorities;
        });
        return converter;
    }
}
