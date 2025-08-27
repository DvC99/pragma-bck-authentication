package co.com.crediya.config;

import org.reactivecommons.utils.ObjectMapper;
import org.reactivecommons.utils.ObjectMapperImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the object mapper.
 */
@Configuration
public class ObjectMapperConfig {

    /**
     * Creates an object mapper bean.
     *
     * @return the object mapper bean
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapperImp();
    }

}
