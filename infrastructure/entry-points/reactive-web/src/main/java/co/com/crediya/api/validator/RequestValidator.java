package co.com.crediya.api.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * A generic request validator that uses Jakarta Bean Validation.
 */
@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    /**
     * Validates a given object.
     *
     * @param object the object to validate
     * @param <T>    the type of the object
     * @return a Mono containing the valid object, or a Mono.error with a ConstraintViolationException if validation fails
     */
    public <T> Mono<T> validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (violations.isEmpty()) {
            return Mono.just(object);
        }
        return Mono.error(new ConstraintViolationException(violations));
    }
}
