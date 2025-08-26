package co.com.crediya.r2dbc.repository.usuario;

import co.com.crediya.model.exceptions.RepositoryException;
import co.com.crediya.model.rol.gateways.RolRepository;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.r2dbc.entity.UsuarioEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter for the repository of usuarios.
 */
@Repository
public class UsuarioRepositoryAdapter extends ReactiveAdapterOperations<Usuario, UsuarioEntity, Integer, UsuarioReactiveRepository> implements UsuarioRepository {

    private final RolRepository rolRepository;

    /**
     * Constructor for the UsuarioRepositoryAdapter.
     *
     * @param repository    the reactive repository
     * @param mapper        the object mapper
     * @param rolRepository the rol repository
     */
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

    @Override
    public Mono<Usuario> save(Usuario usuario) {
        return super.save(usuario)
                .flatMap(this::loadRolForUsuario)
                .onErrorMap(DataAccessException.class, e -> new RepositoryException("Error guardando usuario en la base de datos", e));
    }

    @Override
    public Flux<Usuario> findAll() {
        return super.findAll()
                .flatMap(this::loadRolForUsuario)
                .onErrorMap(DataAccessException.class, e -> new RepositoryException("Error buscando todos los usuarios en la base de datos", e));
    }

    @Override
    public Mono<Usuario> findById(Long id) {
        return super.findById(id.intValue())
                .flatMap(this::loadRolForUsuario)
                .onErrorMap(DataAccessException.class, e -> new RepositoryException("Error buscando usuario por id en la base de datos", e));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id.intValue())
                .onErrorMap(DataAccessException.class, e -> new RepositoryException("Error eliminando usuario por id en la base de datos", e));
    }

    @Override
    public Mono<Usuario> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(data -> mapper.map(data, Usuario.class))
                .flatMap(this::loadRolForUsuario)
                .onErrorMap(DataAccessException.class, e -> new RepositoryException("Error buscando usuario por email en la base de datos", e));
    }

    @Override
    public Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad) {
        return repository.findByDocumentoIdentidad(documentoIdentidad)
                .map(data -> mapper.map(data, Usuario.class))
                .flatMap(this::loadRolForUsuario)
                .onErrorMap(DataAccessException.class, e -> new RepositoryException("Error buscando usuario por documento en la base de datos", e));
    }

    private Mono<Usuario> loadRolForUsuario(Usuario usuario) {
        if (usuario.getRol() != null && usuario.getRol().getId() != null) {
            return rolRepository.findById(usuario.getRol().getId().longValue())
                    .map(rol -> {
                        usuario.setRol(rol);
                        return usuario;
                    })
                    .defaultIfEmpty(usuario);
        }
        return Mono.just(usuario);
    }
}
