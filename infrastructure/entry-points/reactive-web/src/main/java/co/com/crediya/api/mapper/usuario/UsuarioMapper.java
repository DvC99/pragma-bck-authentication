package co.com.crediya.api.mapper.usuario;

import co.com.crediya.api.dto.rol.RolDTO;
import co.com.crediya.api.dto.usuario.UsuarioDTO;
import co.com.crediya.model.rol.Rol;
import co.com.crediya.model.usuario.Usuario;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Usuario and UsuarioDTO objects.
 */
@Component
public class UsuarioMapper {

    /**
     * Converts a UsuarioDTO to a Usuario domain model.
     *
     * @param usuarioDTO the UsuarioDTO to convert
     * @return the converted Usuario domain model
     */
    public Usuario toModel(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) {
            return null;
        }
        return Usuario.builder()
                .id(usuarioDTO.id())
                .nombres(usuarioDTO.nombres())
                .apellidos(usuarioDTO.apellidos())
                .fechaNacimiento(usuarioDTO.fechaNacimiento())
                .email(usuarioDTO.email())
                .documentoIdentidad(usuarioDTO.documentoIdentidad())
                .telefono(usuarioDTO.telefono())
                .salarioBase(usuarioDTO.salarioBase())
                .rol(toModel(usuarioDTO.rol()))
                .build();
    }

    /**
     * Converts a Usuario domain model to a UsuarioDTO.
     *
     * @param usuario the Usuario domain model to convert
     * @return the converted UsuarioDTO
     */
    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getFechaNacimiento(),
                usuario.getEmail(),
                usuario.getDocumentoIdentidad(),
                usuario.getTelefono(),
                usuario.getSalarioBase(),
                toDTO(usuario.getRol())
        );
    }

    private Rol toModel(RolDTO rolDTO) {
        if (rolDTO == null) {
            return null;
        }
        return Rol.builder()
                .id(rolDTO.id())
                .nombre(rolDTO.nombre())
                .descripcion(rolDTO.descripcion())
                .build();
    }

    private RolDTO toDTO(Rol rol) {
        if (rol == null) {
            return null;
        }
        return new RolDTO(
                rol.getId(),
                rol.getNombre(),
                rol.getDescripcion()
        );
    }
}

