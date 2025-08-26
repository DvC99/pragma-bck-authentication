package co.com.crediya.r2dbc.repository.rol;

import co.com.crediya.model.rol.Rol;
import co.com.crediya.model.rol.gateways.RolRepository;
import co.com.crediya.r2dbc.entity.RolEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter for the repository of roles.
 */
@Repository
public class RolRepositoryAdapter extends ReactiveAdapterOperations<Rol, RolEntity, Integer, RolReactiveRepository > implements RolRepository {

    /**
     * Constructor for the RolRepositoryAdapter.
     *
     * @param repository the reactive repository
     * @param mapper     the object mapper
     */
    @Autowired
    public RolRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, Rol.class));
    }

    @Override
    public Mono<Rol> save(Rol rol) {
        return super.save(rol);
    }

    @Override
    public Flux<Rol> findAll() {
        return super.findAll();
    }
    @Override
    public Mono<Rol> findById(Long id) {
        return super.findById(id.intValue());
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id.intValue());
    }
}
