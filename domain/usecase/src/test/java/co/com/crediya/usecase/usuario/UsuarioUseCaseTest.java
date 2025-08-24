package co.com.crediya.usecase.usuario;

import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario validUsuario;
    private Usuario existingUsuario;

    @BeforeEach
    void setUp() {
        validUsuario = Usuario.builder()
                .nombres("John")
                .apellidos("Doe")
                .fechaNacimiento(Date.from(LocalDate.of(1990, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .email("john.doe@example.com")
                .documentoIdentidad("123456789")
                .telefono("1234567890")
                .salarioBase(new BigDecimal("5000000"))
                .idRol(1L)
                .nombreRol("Solicitante")
                .build();

        existingUsuario = validUsuario.toBuilder().id(1).build();
    }

    @Test
    void saveUsuario_success() {
        when(usuarioRepository.findByEmail(any(String.class))).thenReturn(Mono.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(validUsuario));

        StepVerifier.create(usuarioUseCase.saveUsuario(validUsuario))
                .expectNext(validUsuario)
                .verifyComplete();
    }

    @Test
    void saveUsuario_existingEmail_shouldThrowError() {
        when(usuarioRepository.findByEmail(any(String.class))).thenReturn(Mono.just(existingUsuario));

        StepVerifier.create(usuarioUseCase.saveUsuario(validUsuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El correo electrónico ya está registrado"))
                .verify();
    }

    @Test
    void saveUsuario_blankNombres_shouldThrowError() {
        validUsuario.setNombres("");
        StepVerifier.create(usuarioUseCase.saveUsuario(validUsuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El nombre no puede estar vacío"))
                .verify();
    }

    @Test
    void saveUsuario_nullApellidos_shouldThrowError() {
        validUsuario.setApellidos(null);
        StepVerifier.create(usuarioUseCase.saveUsuario(validUsuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Los apellidos no pueden estar vacíos"))
                .verify();
    }

    @Test
    void saveUsuario_invalidEmail_shouldThrowError() {
        validUsuario.setEmail("invalid-email");
        StepVerifier.create(usuarioUseCase.saveUsuario(validUsuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El email no es válido"))
                .verify();
    }

    @Test
    void saveUsuario_nullFechaNacimiento_shouldThrowError() {
        validUsuario.setFechaNacimiento(null);
        StepVerifier.create(usuarioUseCase.saveUsuario(validUsuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("La fecha de nacimiento no puede estar vacía"))
                .verify();
    }

    @Test
    void saveUsuario_futureFechaNacimiento_shouldThrowError() {
        validUsuario.setFechaNacimiento(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        StepVerifier.create(usuarioUseCase.saveUsuario(validUsuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("La fecha de nacimiento no puede ser futura"))
                .verify();
    }

    @Test
    void saveUsuario_nullSalarioBase_shouldThrowError() {
        validUsuario.setSalarioBase(null);
        StepVerifier.create(usuarioUseCase.saveUsuario(validUsuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El salario base no puede estar vacío"))
                .verify();
    }

    @Test
    void saveUsuario_salarioBaseBelowMin_shouldThrowError() {
        validUsuario.setSalarioBase(new BigDecimal("-100"));
        StepVerifier.create(usuarioUseCase.saveUsuario(validUsuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El salario base debe estar entre 0 y 15,000,000"))
                .verify();
    }

    @Test
    void saveUsuario_salarioBaseAboveMax_shouldThrowError() {
        validUsuario.setSalarioBase(new BigDecimal("15000001"));
        StepVerifier.create(usuarioUseCase.saveUsuario(validUsuario))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El salario base debe estar entre 0 y 15,000,000"))
                .verify();
    }

    @Test
    void getAllUsuarios_success() {
        when(usuarioRepository.findAll()).thenReturn(Flux.just(validUsuario, existingUsuario));

        StepVerifier.create(usuarioUseCase.getAllUsuarios())
                .expectNext(validUsuario, existingUsuario)
                .verifyComplete();
    }

    @Test
    void getUsuarioById_success() {
        when(usuarioRepository.findById(any(Long.class))).thenReturn(Mono.just(validUsuario));

        StepVerifier.create(usuarioUseCase.getUsuarioById(1L))
                .expectNext(validUsuario)
                .verifyComplete();
    }

    @Test
    void deleteUsuario_success() {
        when(usuarioRepository.deleteById(any(Long.class))).thenReturn(Mono.empty());

        StepVerifier.create(usuarioUseCase.deleteUsuario(1L))
                .verifyComplete();
    }
}