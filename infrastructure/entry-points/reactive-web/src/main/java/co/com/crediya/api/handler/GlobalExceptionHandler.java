package co.com.crediya.api.handler;

import co.com.crediya.api.dto.ApiResponse;
import co.com.crediya.model.exceptions.DocumentoIdentidadAlreadyExistsException;
import co.com.crediya.model.exceptions.DomainException;
import co.com.crediya.model.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.exceptions.InfrastructureException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * This class handles exceptions thrown by the application and returns a standardized API response.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions for request bodies.
     *
     * @param ex the exception
     * @return a Mono containing the server response with a 400 status code
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<Map<String, String>>>> handleValidationExceptions(WebExchangeBindException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Error de validación"
                ));

        ApiResponse<Map<String, String>> apiResponse = ApiResponse.<Map<String, String>>builder()
                .codigo(HttpStatus.BAD_REQUEST.value())
                .mensaje("Error de validación de infraestructura")
                .body(errors)
                .build();

        return Mono.just(ResponseEntity.badRequest().body(apiResponse));
    }

    /**
     * Handles constraint violation exceptions from the validation layer.
     *
     * @param ex the exception
     * @return a Mono containing the server response with a 400 status code
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ApiResponse<Map<String, String>>>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        ApiResponse<Map<String, String>> apiResponse = ApiResponse.<Map<String, String>>builder()
                .codigo(HttpStatus.BAD_REQUEST.value())
                .mensaje("Error de validación de infraestructura")
                .body(errors)
                .build();

        return Mono.just(ResponseEntity.badRequest().body(apiResponse));
    }

    /**
     * Handles the specific domain exception for when an email already exists.
     *
     * @param ex the exception
     * @return a Mono containing the server response with a 409 status code
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .codigo(HttpStatus.CONFLICT.value())
                .mensaje(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse));
    }

    /**
     * Handles the specific domain exception for when a document already exists.
     *
     * @param ex the exception
     * @return a Mono containing the server response with a 409 status code
     */
    @ExceptionHandler(DocumentoIdentidadAlreadyExistsException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleDocumentoIdentidadAlreadyExistsException(DocumentoIdentidadAlreadyExistsException ex) {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .codigo(HttpStatus.CONFLICT.value())
                .mensaje(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse));
    }

    /**
     * Handles generic domain exceptions as a fallback.
     *
     * @param ex the exception
     * @return a Mono containing the server response with a 400 status code
     */
    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleDomainException(DomainException ex) {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .codigo(HttpStatus.BAD_REQUEST.value())
                .mensaje("Error de negocio: " + ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.badRequest().body(apiResponse));
    }

    /**
     * Handles generic infrastructure exceptions as a fallback.
     *
     * @param ex the exception
     * @return a Mono containing the server response with a 500 status code
     */
    @ExceptionHandler(InfrastructureException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleInfrastructureException(InfrastructureException ex) {
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .codigo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .mensaje("Error de infraestructura: " + ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse));
    }
}

