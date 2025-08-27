package co.com.crediya.model.usuario;

import co.com.crediya.model.rol.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Usuario {
    private Integer id;

    private String nombres;

    private String apellidos;

    private Date fechaNacimiento;

    private String email;

    private String documentoIdentidad;

    private String telefono;

    private String direccion;

    private BigDecimal salarioBase;

    private Rol rol; // Nuevo campo de tipo Rol

    // Datos de auditoría
    private String createdBy;

    private String modifiedBy;

    private LocalDateTime dateCreated;

    private LocalDateTime dateModified;
}