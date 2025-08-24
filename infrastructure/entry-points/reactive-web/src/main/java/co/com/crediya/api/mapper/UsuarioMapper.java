package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.RolDTO;
import co.com.crediya.api.dto.UsuarioDTO;
import co.com.crediya.model.rol.Rol;
import co.com.crediya.model.usuario.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toModel(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) {
            return null;
        }
        return Usuario.builder()
                .id(usuarioDTO.getId())
                .nombres(usuarioDTO.getNombres())
                .apellidos(usuarioDTO.getApellidos())
                .fechaNacimiento(usuarioDTO.getFechaNacimiento())
                .email(usuarioDTO.getEmail())
                .documentoIdentidad(usuarioDTO.getDocumentoIdentidad())
                .telefono(usuarioDTO.getTelefono())
                .salarioBase(usuarioDTO.getSalarioBase())
                .rol(toModel(usuarioDTO.getRol()))
                .build();
    }

    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .email(usuario.getEmail())
                .documentoIdentidad(usuario.getDocumentoIdentidad())
                .telefono(usuario.getTelefono())
                .salarioBase(usuario.getSalarioBase())
                .rol(toDTO(usuario.getRol()))
                .build();
    }

    private Rol toModel(RolDTO rolDTO) {
        if (rolDTO == null) {
            return null;
        }
        return Rol.builder()
                .id(rolDTO.getId())
                .nombre(rolDTO.getNombre())
                .descripcion(rolDTO.getDescripcion())
                .build();
    }

    private RolDTO toDTO(Rol rol) {
        if (rol == null) {
            return null;
        }
        return RolDTO.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .build();
    }
}
