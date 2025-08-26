package co.com.crediya;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Main application class.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@OpenAPIDefinition(info = @Info(title = "API de Gestión de Usuarios", version = "1.0", description = "Documentación de la API para la gestión de usuarios en el sistema Crediya"))
public class MainApplication {
    /**
     * Main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
