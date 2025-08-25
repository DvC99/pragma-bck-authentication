package co.com.crediya.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RolDTO {
    @NotNull(message = "El ID del rol no puede ser nulo")
    private Integer id;
    private String nombre;
    private String descripcion;
}
