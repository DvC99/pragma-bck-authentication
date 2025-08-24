package co.com.crediya.usecase.usuario;

import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UsuarioUseCase {
    private final UsuarioRepository usuarioRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public Mono<Usuario> saveUsuario(Usuario usuario) {
        return validateUsuario(usuario) // First, validate the user object
                .flatMap(validatedUsuario -> usuarioRepository.findByEmail(validatedUsuario.getEmail())
                        .flatMap(existingUser -> Mono.error(new IllegalArgumentException("El correo electrónico ya está registrado")).cast(Usuario.class))
                        .switchIfEmpty(Mono.defer(() -> usuarioRepository.save(validatedUsuario))));
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

    private Mono<Usuario> validateUsuario(Usuario usuario) {
        if (usuario.getNombres() == null || usuario.getNombres().isBlank()) {
            return Mono.error(new IllegalArgumentException("El nombre no puede estar vacío"));
        }
        if (usuario.getApellidos() == null || usuario.getApellidos().isBlank()) {
            return Mono.error(new IllegalArgumentException("Los apellidos no pueden estar vacíos"));
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            return Mono.error(new IllegalArgumentException("El email no puede estar vacío"));
        }
        if (!EMAIL_PATTERN.matcher(usuario.getEmail()).matches()) {
            return Mono.error(new IllegalArgumentException("El email no es válido"));
        }
        if (usuario.getDocumentoIdentidad() == null || usuario.getDocumentoIdentidad().isBlank()) {
            return Mono.error(new IllegalArgumentException("El documento de identidad no puede estar vacío"));
        }
        if (usuario.getTelefono() == null || usuario.getTelefono().isBlank()) {
            return Mono.error(new IllegalArgumentException("El teléfono no puede estar vacío"));
        }
        if (usuario.getFechaNacimiento() == null) {
            return Mono.error(new IllegalArgumentException("La fecha de nacimiento no puede estar vacía"));
        }
        if (usuario.getFechaNacimiento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now())) {
            return Mono.error(new IllegalArgumentException("La fecha de nacimiento no puede ser futura"));
        }
        if (usuario.getSalarioBase() == null) {
            return Mono.error(new IllegalArgumentException("El salario base no puede estar vacío"));
        }
        if (usuario.getSalarioBase().compareTo(BigDecimal.ZERO) < 0 || usuario.getSalarioBase().compareTo(new BigDecimal("15000000")) > 0) {
            return Mono.error(new IllegalArgumentException("El salario base debe estar entre 0 y 15,000,000"));
        }
        return Mono.just(usuario);
    }
}