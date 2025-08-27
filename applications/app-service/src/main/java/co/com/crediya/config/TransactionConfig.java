package co.com.crediya.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Enables reactive transaction management for the application.
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {
}
