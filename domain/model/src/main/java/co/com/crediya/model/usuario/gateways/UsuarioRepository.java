package co.com.crediya.model.usuario.gateways;

import co.com.crediya.model.usuario.Usuario;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Usuario> save(Usuario task);

    Flux<Usuario> findAll();

    Mono<Usuario> findById(Long id);

    Mono<Void> deleteById(Long id);

    Mono<Usuario> findByEmail(String email);
}
