package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a standardized API response structure.
 * @param <T> the type of the data in the response body
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    /**
     * The HTTP status code of the response.
     */
    private int codigo;
    /**
     * A descriptive message about the response.
     */
    private String mensaje;
    /**
     * The body of the response, containing the requested data.
     */
    private T body;
}
