package iuh.fit.se.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {
    @Bean
    public OpenAPI defineOpenApi() {
        // 1. Cấu hình SecurityScheme cho Bearer Token
        Components components = new Components()
                .addSecuritySchemes("BearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT") // Chỉ định định dạng là JWT
                        .description("Nhập Bearer Token (ví dụ: Bearer <token>) để truy cập các API yêu cầu xác thực."));

        // 2. Định nghĩa SecurityRequirement
        // Điều này áp dụng BearerAuth cho tất cả các API
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        // 3. Cấu hình Server và Info như code ban đầu
        Server server = new Server();
        server.setUrl("/api");
        server.setDescription("Employee Management REST API Documentation");

        Info information = new Info()
                .title("Employee Management REST API Documentation")
                .version("1.0")
                .description("This API exposes endpoints to manage employees.");

        // 4. Trả về OpenAPI với Components và SecurityRequirement
        return new OpenAPI()
                .info(information)
                .servers(List.of(server))
                .components(components) // Thêm SecurityScheme vào Components
                .addSecurityItem(securityRequirement); // Thêm SecurityRequirement
    }
}