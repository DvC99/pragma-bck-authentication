package co.com.crediya.r2dbc.repository.usuario;

import co.com.crediya.model.rol.gateways.RolRepository;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.r2dbc.entity.UsuarioEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UsuarioRepositoryAdapter extends ReactiveAdapterOperations<Usuario, UsuarioEntity, Integer, UsuarioReactiveRepository > implements UsuarioRepository {

    private final RolRepository rolRepository;

    @Autowired
    public UsuarioRepositoryAdapter(UsuarioReactiveRepository repository, ObjectMapper mapper, RolRepository rolRepository) {
        super(repository, mapper, data -> mapper.map(data, Usuario.class)); // toEntityFn ahora solo mapea la entidad base
        this.rolRepository = rolRepository;
    }

    @Override
    protected UsuarioEntity toData(Usuario entity) {
        UsuarioEntity usuarioEntity = mapper.map(entity, UsuarioEntity.class);
        if (entity.getRol() != null && entity.getRol().getId() != null) {
            usuarioEntity.setIdRol(entity.getRol().getId());
        }
        return usuarioEntity;
    }

    // Sobrescribir los métodos públicos para cargar el Rol de forma reactiva
    @Override
    public Mono<Usuario> save(Usuario usuario) {
        return super.save(usuario)
                .flatMap(this::loadRolForUsuario); // Cargar el Rol después de guardar
    }

    @Override
    public Flux<Usuario> findAll() {
        return super.findAll()
                .flatMap(this::loadRolForUsuario); // Cargar el Rol para cada usuario
    }

    @Override
    public Mono<Usuario> findById(Long id) {
        return super.findById(id.intValue())
                .flatMap(this::loadRolForUsuario); // Cargar el Rol para el usuario encontrado
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id.intValue());
    }

    public Mono<Usuario> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(data -> mapper.map(data, Usuario.class)) // Mapear a Usuario base
                .flatMap(this::loadRolForUsuario); // Cargar el Rol para el usuario encontrado
    }

    private Mono<Usuario> loadRolForUsuario(Usuario usuario) {
        if (usuario.getRol() != null && usuario.getRol().getId() != null) {
            return rolRepository.findById(usuario.getRol().getId().longValue())
                    .map(rol -> {
                        usuario.setRol(rol);
                        return usuario;
                    })
                    .defaultIfEmpty(usuario); // Si no se encuentra el rol, devuelve el usuario sin rol
        }
        return Mono.just(usuario);
    }
}
