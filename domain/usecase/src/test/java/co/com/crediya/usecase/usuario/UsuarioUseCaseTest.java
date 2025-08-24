package co.com.crediya.usecase.usuario;

import co.com.crediya.model.rol.Rol;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuarioValido;
    private Rol rolValido;

    @BeforeEach
    void setUp() {
        rolValido = Rol.builder()
                .id(1)
                .nombre("CLIENTE")
                .descripcion("Rol para clientes")
                .build();

        usuarioValido = Usuario.builder()
                .id(1)
                .nombres("John")
                .apellidos("Doe")
                .email("john.doe@example.com")
                .documentoIdentidad("123456789")
                .telefono("3001234567")
                .fechaNacimiento(Date.from(LocalDate.of(1990, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .salarioBase(new BigDecimal("2500000"))
                .rol(rolValido)
                .build();
    }

    @Nested
    @DisplayName("Pruebas para saveUsuario")
    class SaveUsuarioTests {

        @Test
        @DisplayName("Debe guardar un usuario exitosamente cuando el email no existe")
        void saveUsuario_Success() {
            // Arrange
            when(usuarioRepository.findByEmail(usuarioValido.getEmail())).thenReturn(Mono.empty());
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuarioValido));

            // Act
            Mono<Usuario> result = usuarioUseCase.saveUsuario(usuarioValido);

            // Assert
            StepVerifier.create(result)
                    .expectNext(usuarioValido)
                    .verifyComplete();

            verify(usuarioRepository).findByEmail(usuarioValido.getEmail());
            verify(usuarioRepository).save(usuarioValido);
        }

        @Test
        @DisplayName("Debe fallar al guardar si el correo electrónico ya está registrado")
        void saveUsuario_EmailAlreadyExists() {
            // Arrange
            when(usuarioRepository.findByEmail(usuarioValido.getEmail())).thenReturn(Mono.just(usuarioValido));

            // Act
            Mono<Usuario> result = usuarioUseCase.saveUsuario(usuarioValido);

            // Assert
            StepVerifier.create(result)
                    .expectError(IllegalArgumentException.class)
                    .verify();
        }
    }

    @Test
    @DisplayName("Debe actualizar un usuario exitosamente")
    void updateUsuario_Success() {
        // Arrange
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuarioValido));

        // Act
        Mono<Usuario> result = usuarioUseCase.updateUsuario(usuarioValido);

        // Assert
        StepVerifier.create(result)
                .expectNext(usuarioValido)
                .verifyComplete();
        verify(usuarioRepository).save(usuarioValido);
    }

    @Test
    @DisplayName("Debe obtener todos los usuarios")
    void getAllUsuarios_Success() {
        // Arrange
        Usuario usuario2 = Usuario.builder()
                .id(2)
                .nombres("Jane")
                .rol(rolValido)
                .build();
        when(usuarioRepository.findAll()).thenReturn(Flux.just(usuarioValido, usuario2));

        // Act
        Flux<Usuario> result = usuarioUseCase.getAllUsuarios();

        // Assert
        StepVerifier.create(result)
                .expectNext(usuarioValido)
                .expectNext(usuario2)
                .verifyComplete();
    }

    @Nested
    @DisplayName("Pruebas para getUsuarioById")
    class GetUsuarioByIdTests {

        @Test
        @DisplayName("Debe obtener un usuario por su ID")
        void getUsuarioById_Success() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Mono.just(usuarioValido));

            // Act
            Mono<Usuario> result = usuarioUseCase.getUsuarioById(1L);

            // Assert
            StepVerifier.create(result)
                    .expectNext(usuarioValido)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe retornar Mono.empty si el usuario no se encuentra")
        void getUsuarioById_NotFound() {
            // Arrange
            when(usuarioRepository.findById(99L)).thenReturn(Mono.empty());

            // Act
            Mono<Usuario> result = usuarioUseCase.getUsuarioById(99L);

            // Assert
            StepVerifier.create(result)
                    .verifyComplete();
        }
    }

    @Test
    @DisplayName("Debe eliminar un usuario por su ID")
    void deleteUsuario_Success() {
        // Arrange
        when(usuarioRepository.deleteById(1L)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = usuarioUseCase.deleteUsuario(1L);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
        verify(usuarioRepository).deleteById(1L);
    }
}