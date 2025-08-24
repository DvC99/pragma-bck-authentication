package co.com.crediya.model.rol.gateways;

import co.com.crediya.model.rol.Rol;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Rol> save(Rol task);

    Flux<Rol> findAll();

    Mono<Rol> findById(Long id);

    Mono<Void> deleteById(Long id);
}
