package co.com.crediya.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    /**
     * Crea un bean de tipo OpenAPI personalizado.
     * Configura la información básica de la API.
     *
     * @return una instancia de OpenAPI configurada.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(apiInfo());
    }

    /**
     * Configura la información básica de la API.
     * Define el título, la descripción y la versión de la API.
     *
     * @return una instancia de Info con los detalles de la API.
     */
    private Info apiInfo() {
        return new Info()
                .title("CrediYa Authentication API")
                .version("1.0")
                .description("API documentation for the CrediYa Authentication microservice.");
    }
}
