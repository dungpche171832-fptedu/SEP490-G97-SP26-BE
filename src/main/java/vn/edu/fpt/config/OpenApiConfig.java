package vn.edu.fpt.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Clinic Management System API",
                version = "1.0",
                description = "API quản lý phòng khám - Hệ thống đặt lịch khám, quản lý bệnh nhân, bác sĩ",
                contact = @Contact(
                        name = "G2 Team",
                        email = "support@clinic.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Server"),
                @Server(url = "https://api.clinic.com", description = "Production Server")
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Token Authentication - Nhập token vào đây (không cần thêm 'Bearer ')",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}

