package co.com.crediya.usecase.usuario;

import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    public Mono<Usuario> saveUsuario(Usuario usuario) {
        // La validación de campos se hace ahora en la capa de entrada (Handler) a través de anotaciones.
        // El caso de uso se centra en la lógica de negocio, como verificar la unicidad del email.
        return usuarioRepository.findByEmail(usuario.getEmail())
                .flatMap(existingUser -> Mono.error(new IllegalArgumentException("El correo electrónico ya está registrado")))
                .switchIfEmpty(Mono.defer(() -> usuarioRepository.save(usuario)))
                .cast(Usuario.class);
    }

    public Mono<Usuario> updateUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Flux<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Mono<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Mono<Void> deleteUsuario(Long id) {
        return usuarioRepository.deleteById(id);
    }
}
