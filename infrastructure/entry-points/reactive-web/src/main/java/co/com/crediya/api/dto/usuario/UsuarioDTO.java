package co.com.crediya.api.dto.usuario;

import co.com.crediya.api.dto.rol.RolDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Data Transfer Object for Usuario information.
 *
 * @param id                 the unique identifier of the user
 * @param nombres            the user's first names
 * @param apellidos          the user's last names
 * @param fechaNacimiento    the user's date of birth
 * @param email              the user's email address
 * @param documentoIdentidad the user's identity document number
 * @param telefono           the user's phone number
 * @param direccion          the user's address
 * @param salarioBase        the user's base salary
 * @param rol                the user's role
 */
public record UsuarioDTO(
        Integer id,

        @NotBlank(message = "El nombre no puede estar vacío")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "Los nombres solo deben contener letras y espacios")
        String nombres,

        @NotBlank(message = "Los apellidos no pueden estar vacíos")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "Los apellidos solo deben contener letras y espacios")
        String apellidos,

        @NotNull(message = "La fecha de nacimiento no puede estar vacía")
        @Past(message = "La fecha de nacimiento no puede ser futura")
        Date fechaNacimiento,

        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "El email no es válido")
        String email,

        @NotBlank(message = "El documento de identidad no puede estar vacío")
        @Pattern(regexp = "^[0-9]{5,20}$", message = "El documento de identidad debe contener solo números y tener entre 5 y 20 dígitos")
        String documentoIdentidad,

        @NotBlank(message = "El teléfono no puede estar vacío")
        @Pattern(regexp = "^[0-9]{7,15}$", message = "El teléfono debe contener solo números y tener entre 7 y 15 dígitos")
        String telefono,

        @NotBlank(message = "La dirección no puede estar vacía")
        String direccion,

        @NotNull(message = "El salario base no puede estar vacío")
        @DecimalMin(value = "0", message = "El salario base debe ser como mínimo 0")
        @DecimalMax(value = "15000000", message = "El salario base debe ser como máximo 15,000,000")
        BigDecimal salarioBase,

        @NotNull(message = "El rol del usuario no puede ser nulo")
        @Valid
        RolDTO rol
) {
}

