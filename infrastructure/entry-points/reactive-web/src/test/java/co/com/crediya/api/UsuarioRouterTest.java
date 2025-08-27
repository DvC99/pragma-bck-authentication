package co.com.crediya.api;

import co.com.crediya.api.dto.rol.RolDTO;
import co.com.crediya.api.dto.usuario.UsuarioDTO;
import co.com.crediya.api.handler.GlobalExceptionHandler;
import co.com.crediya.api.handler.usuario.UsuarioHandler;
import co.com.crediya.api.mapper.usuario.UsuarioMapper;
import co.com.crediya.api.router.rol.UsuarioRouter;
import co.com.crediya.api.validator.RequestValidator;
import co.com.crediya.model.exceptions.DocumentoIdentidadAlreadyExistsException;
import co.com.crediya.model.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.rol.Rol;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        UsuarioRouter.class,
        UsuarioHandler.class,
        GlobalExceptionHandler.class,
        UsuarioMapper.class,
        RequestValidator.class
})
@WebFluxTest
class UsuarioRouterTest {

    @Autowired
    private ApplicationContext context;

    private WebTestClient webTestClient;

    @MockitoBean
    private UsuarioUseCase usuarioUseCase;

    @MockitoBean
    private TransactionalOperator transactionalOperator;

    private UsuarioDTO validUsuarioDTO;
    private Usuario validUsuario;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();

        RolDTO rolDTO = new RolDTO(1, "CLIENTE", "Rol para clientes");
        validUsuarioDTO = new UsuarioDTO(
                1,
                "Jane",
                "Doe",
                Date.from(LocalDate.of(1995, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "jane.doe@example.com",
                "987654321",
                "3109876543",
                "Calle Falsa 123",
                new BigDecimal("6000000"),
                rolDTO
        );

        validUsuario = Usuario.builder()
                .id(1)
                .nombres("Jane")
                .apellidos("Doe")
                .fechaNacimiento(Date.from(LocalDate.of(1995, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .email("jane.doe@example.com")
                .documentoIdentidad("987654321")
                .telefono("3109876543")
                .direccion("Calle Falsa 123")
                .salarioBase(new BigDecimal("6000000"))
                .rol(Rol.builder().id(1).nombre("CLIENTE").descripcion("Rol para clientes").build())
                .build();

        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    @DisplayName("POST /api/v1/usuarios")
    class PostUsuario {
        @Test
        @DisplayName("Debe guardar un usuario exitosamente")
        void shouldSaveUsuarioSuccessfully() {
            // Arrange
            when(usuarioUseCase.saveUsuario(any(Usuario.class))).thenReturn(Mono.just(validUsuario));

            // Act & Assert
            webTestClient.post()
                    .uri("/api/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(validUsuarioDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.codigo").isEqualTo(200)
                    .jsonPath("$.mensaje").isEqualTo("Usuario guardado exitosamente")
                    .jsonPath("$.body.id").isEqualTo(validUsuario.getId())
                    .jsonPath("$.body.nombres").isEqualTo(validUsuario.getNombres());
        }

        @Test
        @DisplayName("Debe devolver 400 por nombre inválido")
        void shouldReturnBadRequestForInvalidNombres() {
            // Arrange
            UsuarioDTO invalidDto = new UsuarioDTO(null, "Jane123", "Doe", validUsuarioDTO.fechaNacimiento(), validUsuarioDTO.email(), validUsuarioDTO.documentoIdentidad(), validUsuarioDTO.telefono(), "Calle Falsa 123", validUsuarioDTO.salarioBase(), validUsuarioDTO.rol());

            // Act & Assert
            webTestClient.post()
                    .uri("/api/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.codigo").isEqualTo(400)
                    .jsonPath("$.mensaje").isEqualTo("Error de validación de infraestructura")
                    .jsonPath("$.body.nombres").isEqualTo("Los nombres solo deben contener letras y espacios");
        }

        @Test
        @DisplayName("Debe devolver 409 por email existente")
        void shouldReturnConflictForExistingEmail() {
            // Arrange
            when(usuarioUseCase.saveUsuario(any(Usuario.class)))
                    .thenReturn(Mono.error(new EmailAlreadyExistsException("El correo electrónico ya está registrado")));

            // Act & Assert
            webTestClient.post()
                    .uri("/api/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(validUsuarioDTO)
                    .exchange()
                    .expectStatus().isEqualTo(409)
                    .expectBody()
                    .jsonPath("$.codigo").isEqualTo(409)
                    .jsonPath("$.mensaje").isEqualTo("El correo electrónico ya está registrado");
        }

        @Test
        @DisplayName("Debe devolver 409 por documento existente")
        void shouldReturnConflictForExistingDocumento() {
            // Arrange
            when(usuarioUseCase.saveUsuario(any(Usuario.class)))
                    .thenReturn(Mono.error(new DocumentoIdentidadAlreadyExistsException("El documento de identidad ya está registrado")));

            // Act & Assert
            webTestClient.post()
                    .uri("/api/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(validUsuarioDTO)
                    .exchange()
                    .expectStatus().isEqualTo(409)
                    .expectBody()
                    .jsonPath("$.codigo").isEqualTo(409)
                    .jsonPath("$.mensaje").isEqualTo("El documento de identidad ya está registrado");
        }
    }

    @Nested
    @DisplayName("GET /api/v1/usuarios")
    class GetUsuarios {
        @Test
        @DisplayName("Debe obtener todos los usuarios")
        void shouldGetAllUsuarios() {
            // Arrange
            when(usuarioUseCase.getAllUsuarios()).thenReturn(Flux.just(validUsuario));

            // Act & Assert
            webTestClient.get()
                    .uri("/api/v1/usuarios")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.codigo").isEqualTo(200)
                    .jsonPath("$.mensaje").isEqualTo("Usuarios obtenidos exitosamente")
                    .jsonPath("$.body[0].nombres").isEqualTo(validUsuario.getNombres());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/usuarios/{id}")
    class GetUsuarioById {
        @Test
        @DisplayName("Debe obtener un usuario por su ID")
        void shouldGetUsuarioById() {
            // Arrange
            when(usuarioUseCase.getUsuarioById(any(Long.class))).thenReturn(Mono.just(validUsuario));

            // Act & Assert
            webTestClient.get()
                    .uri("/api/v1/usuarios/1")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.codigo").isEqualTo(200)
                    .jsonPath("$.mensaje").isEqualTo("Usuario obtenido exitosamente")
                    .jsonPath("$.body.id").isEqualTo(validUsuario.getId());
        }

        @Test
        @DisplayName("Debe devolver 404 si el usuario no se encuentra")
        void shouldReturnNotFoundIfUsuarioNotFound() {
            // Arrange
            when(usuarioUseCase.getUsuarioById(any(Long.class))).thenReturn(Mono.empty());

            // Act & Assert
            webTestClient.get()
                    .uri("/api/v1/usuarios/99")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.codigo").isEqualTo(404)
                    .jsonPath("$.mensaje").isEqualTo("No se encontraron datos para el ID proporcionado: 99");
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/usuarios")
    class PutUsuario {
        @Test
        @DisplayName("Debe actualizar un usuario exitosamente")
        void shouldUpdateUsuario() {
            // Arrange
            when(usuarioUseCase.updateUsuario(any(Usuario.class))).thenReturn(Mono.just(validUsuario));

            // Act & Assert
            webTestClient.put()
                    .uri("/api/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(validUsuarioDTO)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.codigo").isEqualTo(200)
                    .jsonPath("$.mensaje").isEqualTo("Usuario actualizado exitosamente")
                    .jsonPath("$.body.id").isEqualTo(validUsuario.getId());
        }

        @Test
        @DisplayName("Debe devolver 409 por email existente en otro usuario")
        void shouldReturnConflictForExistingEmailInAnotherUser() {
            // Arrange
            when(usuarioUseCase.updateUsuario(any(Usuario.class)))
                    .thenReturn(Mono.error(new EmailAlreadyExistsException("El correo electrónico ya está registrado por otro usuario")));

            // Act & Assert
            webTestClient.put()
                    .uri("/api/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(validUsuarioDTO)
                    .exchange()
                    .expectStatus().isEqualTo(409)
                    .expectBody()
                    .jsonPath("$.codigo").isEqualTo(409)
                    .jsonPath("$.mensaje").isEqualTo("El correo electrónico ya está registrado por otro usuario");
        }

        @Test
        @DisplayName("Debe devolver 409 por documento existente en otro usuario")
        void shouldReturnConflictForExistingDocumentoInAnotherUser() {
            // Arrange
            when(usuarioUseCase.updateUsuario(any(Usuario.class)))
                    .thenReturn(Mono.error(new DocumentoIdentidadAlreadyExistsException("El documento de identidad ya está registrado por otro usuario")));

            // Act & Assert
            webTestClient.put()
                    .uri("/api/v1/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(validUsuarioDTO)
                    .exchange()
                    .expectStatus().isEqualTo(409)
                    .expectBody()
                    .jsonPath("$.codigo").isEqualTo(409)
                    .jsonPath("$.mensaje").isEqualTo("El documento de identidad ya está registrado por otro usuario");
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/usuarios/{id}")
    class DeleteUsuario {
        @Test
        @DisplayName("Debe eliminar un usuario exitosamente")
        void shouldDeleteUsuario() {
            // Arrange
            when(usuarioUseCase.deleteUsuario(any(Long.class))).thenReturn(Mono.empty());

            // Act & Assert
            webTestClient.delete()
                    .uri("/api/v1/usuarios/1")
                    .exchange()
                    .expectStatus().isNoContent();
        }
    }
}
