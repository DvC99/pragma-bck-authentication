package co.com.crediya.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UsuarioDTO {

    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombres;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    private String apellidos;

    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    @Past(message = "La fecha de nacimiento no puede ser futura")
    private Date fechaNacimiento;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email no es válido")
    private String email;

    @NotBlank(message = "El documento de identidad no puede estar vacío")
    private String documentoIdentidad;

    @NotBlank(message = "El teléfono no puede estar vacío")
    private String telefono;

    @NotNull(message = "El salario base no puede estar vacío")
    @DecimalMin(value = "0", message = "El salario base debe ser como mínimo 0")
    @DecimalMax(value = "15000000", message = "El salario base debe ser como máximo 15,000,000")
    private BigDecimal salarioBase;

    @NotNull(message = "El rol del usuario no puede ser nulo")
    @Valid
    private RolDTO rol;
}
