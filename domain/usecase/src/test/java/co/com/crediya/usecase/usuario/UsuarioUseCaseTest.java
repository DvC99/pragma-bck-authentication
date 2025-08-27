package co.com.crediya.usecase.usuario;

import co.com.crediya.model.exceptions.DocumentoIdentidadAlreadyExistsException;
import co.com.crediya.model.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.rol.Rol;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioGateway;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

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
                .direccion("Calle Falsa 123")
                .fechaNacimiento(Date.from(LocalDate.of(1990, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .salarioBase(new BigDecimal("2500000"))
                .rol(rolValido)
                .build();
    }

    @Nested
    @DisplayName("Pruebas para saveUsuario")
    class SaveUsuarioTests {

        @Test
        @DisplayName("Debe guardar un usuario exitosamente")
        void saveUsuario_Success() {
            // Arrange
            when(usuarioGateway.findByEmail(anyString())).thenReturn(Mono.empty());
            when(usuarioGateway.findByDocumentoIdentidad(anyString())).thenReturn(Mono.empty());
            when(usuarioGateway.save(any(Usuario.class))).thenReturn(Mono.just(usuarioValido));

            // Act
            Mono<Usuario> result = usuarioUseCase.saveUsuario(usuarioValido);

            // Assert
            StepVerifier.create(result)
                    .expectNext(usuarioValido)
                    .verifyComplete();
            verify(usuarioGateway).save(usuarioValido);
        }

        @Test
        @DisplayName("Debe fallar si el email ya existe")
        void saveUsuario_EmailAlreadyExists() {
            // Arrange
            when(usuarioGateway.findByEmail(anyString())).thenReturn(Mono.just(usuarioValido));
            // No need to mock findByDocumentoIdentidad as the chain will fail before it's called

            // Act
            Mono<Usuario> result = usuarioUseCase.saveUsuario(usuarioValido);

            // Assert
            StepVerifier.create(result)
                    .expectError(EmailAlreadyExistsException.class)
                    .verify();
            verify(usuarioGateway, never()).findByDocumentoIdentidad(anyString()); // Ensure it's not called
            verify(usuarioGateway, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debe fallar si el documento ya existe")
        void saveUsuario_DocumentoAlreadyExists() {
            // Arrange
            when(usuarioGateway.findByEmail(anyString())).thenReturn(Mono.empty());
            when(usuarioGateway.findByDocumentoIdentidad(anyString())).thenReturn(Mono.just(usuarioValido));

            // Act
            Mono<Usuario> result = usuarioUseCase.saveUsuario(usuarioValido);

            // Assert
            StepVerifier.create(result)
                    .expectError(DocumentoIdentidadAlreadyExistsException.class)
                    .verify();
            verify(usuarioGateway, never()).save(any(Usuario.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para updateUsuario")
    class UpdateUsuarioTests {

        @Test
        @DisplayName("Debe actualizar un usuario exitosamente")
        void updateUsuario_Success() {
            // Arrange
            when(usuarioGateway.findByEmail(anyString())).thenReturn(Mono.empty());
            when(usuarioGateway.findByDocumentoIdentidad(anyString())).thenReturn(Mono.empty());
            when(usuarioGateway.save(any(Usuario.class))).thenReturn(Mono.just(usuarioValido));

            // Act
            Mono<Usuario> result = usuarioUseCase.updateUsuario(usuarioValido);

            // Assert
            StepVerifier.create(result)
                    .expectNext(usuarioValido)
                    .verifyComplete();
            verify(usuarioGateway).save(usuarioValido);
        }

        @Test
        @DisplayName("Debe fallar si el email ya pertenece a otro usuario")
        void updateUsuario_EmailAlreadyExistsInAnotherUser() {
            // Arrange
            Usuario otroUsuario = Usuario.builder()
                    .id(2)
                    .email("john.doe@example.com")
                    .documentoIdentidad("987654321")
                    .build();
            when(usuarioGateway.findByEmail(anyString())).thenReturn(Mono.just(otroUsuario));
            when(usuarioGateway.findByDocumentoIdentidad(anyString())).thenReturn(Mono.empty()); // Mock for the second check

            // Act
            Mono<Usuario> result = usuarioUseCase.updateUsuario(usuarioValido);

            // Assert
            StepVerifier.create(result)
                    .expectError(EmailAlreadyExistsException.class)
                    .verify();
            verify(usuarioGateway, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debe fallar si el documento ya pertenece a otro usuario")
        void updateUsuario_DocumentoAlreadyExistsInAnotherUser() {
            // Arrange
            Usuario otroUsuario = Usuario.builder()
                    .id(2)
                    .email("jane.doe@example.com")
                    .documentoIdentidad("123456789")
                    .build();
            when(usuarioGateway.findByEmail(anyString())).thenReturn(Mono.empty()); // Mock for the first check
            when(usuarioGateway.findByDocumentoIdentidad(anyString())).thenReturn(Mono.just(otroUsuario));

            // Act
            Mono<Usuario> result = usuarioUseCase.updateUsuario(usuarioValido);

            // Assert
            StepVerifier.create(result)
                    .expectError(DocumentoIdentidadAlreadyExistsException.class)
                    .verify();
            verify(usuarioGateway, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debe permitir la actualización si el email le pertenece al mismo usuario")
        void updateUsuario_EmailBelongsToSameUser() {
            // Arrange
            when(usuarioGateway.findByEmail(anyString())).thenReturn(Mono.just(usuarioValido)); // Email belongs to the same user
            when(usuarioGateway.findByDocumentoIdentidad(anyString())).thenReturn(Mono.empty());
            when(usuarioGateway.save(any(Usuario.class))).thenReturn(Mono.just(usuarioValido));

            // Act
            Mono<Usuario> result = usuarioUseCase.updateUsuario(usuarioValido);

            // Assert
            StepVerifier.create(result)
                    .expectNext(usuarioValido)
                    .verifyComplete();
            verify(usuarioGateway).save(usuarioValido);
        }

        @Test
        @DisplayName("Debe permitir la actualización si el documento le pertenece al mismo usuario")
        void updateUsuario_DocumentoBelongsToSameUser() {
            // Arrange
            when(usuarioGateway.findByEmail(anyString())).thenReturn(Mono.empty());
            when(usuarioGateway.findByDocumentoIdentidad(anyString())).thenReturn(Mono.just(usuarioValido)); // Document belongs to the same user
            when(usuarioGateway.save(any(Usuario.class))).thenReturn(Mono.just(usuarioValido));

            // Act
            Mono<Usuario> result = usuarioUseCase.updateUsuario(usuarioValido);

            // Assert
            StepVerifier.create(result)
                    .expectNext(usuarioValido)
                    .verifyComplete();
            verify(usuarioGateway).save(usuarioValido);
        }
    }

    @Test
    @DisplayName("Debe obtener todos los usuarios")
    void getAllUsuarios_Success() {
        // Arrange
        when(usuarioGateway.findAll()).thenReturn(Flux.just(usuarioValido));

        // Act
        Flux<Usuario> result = usuarioUseCase.getAllUsuarios();

        // Assert
        StepVerifier.create(result)
                .expectNext(usuarioValido)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe obtener un usuario por su ID")
    void getUsuarioById_Success() {
        // Arrange
        when(usuarioGateway.findById(anyLong())).thenReturn(Mono.just(usuarioValido));

        // Act
        Mono<Usuario> result = usuarioUseCase.getUsuarioById(1L);

        // Assert
        StepVerifier.create(result)
                .expectNext(usuarioValido)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe eliminar un usuario por su ID")
    void deleteUsuario_Success() {
        // Arrange
        when(usuarioGateway.deleteById(anyLong())).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = usuarioUseCase.deleteUsuario(1L);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
}