package co.com.crediya.r2dbc;

import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.r2dbc.entity.UsuarioEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UsuarioRepositoryAdapter extends ReactiveAdapterOperations<
        Usuario,
        UsuarioEntity,
        Integer,
        UsuarioReactiveRepository
> implements UsuarioRepository {
    public UsuarioRepositoryAdapter(UsuarioReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, Usuario.class));
    }

    @Override
    public Mono<Usuario> save(Usuario usuario) {
        return super.save(usuario);
    }

    @Override
    public Flux<Usuario> findAll() {
        return super.findAll();
    }

    @Override
    public Mono<Usuario> findById(Long id) {
        return super.findById(id.intValue());
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id.intValue());
    }

    public Mono<Usuario> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toEntity);
    }
}