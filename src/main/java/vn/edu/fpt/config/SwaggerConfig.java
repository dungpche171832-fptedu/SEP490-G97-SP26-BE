package vn.edu.fpt.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SwaggerConfig {

    @PostConstruct
    public void logSwaggerUrl() {
        log.info("Swagger UI: http://localhost:8080/swagger-ui/index.html");
    }
}

