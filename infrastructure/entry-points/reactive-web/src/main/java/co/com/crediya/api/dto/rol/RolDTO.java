package co.com.crediya.api.dto.rol;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Rol information.
 *
 * @param id          the unique identifier of the role
 * @param nombre      the name of the role
 * @param descripcion a brief description of the role
 */
public record RolDTO(
        Integer id,
        String nombre,
        String descripcion
) {
}
