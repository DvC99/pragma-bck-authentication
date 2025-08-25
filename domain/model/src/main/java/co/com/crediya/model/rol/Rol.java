package co.com.crediya.model.rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Rol {
    private Integer id;

    private String nombre;

    private String descripcion;

    // Datos de auditoría
    private String createdBy;

    private String modifiedBy;

    private LocalDateTime dateCreated;

    private LocalDateTime dateModified;
}
