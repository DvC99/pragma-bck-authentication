package co.com.crediya.api;

import co.com.crediya.api.config.UsuarioPath;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.com.crediya.api.handler.GlobalExceptionHandler;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalExceptionHandler.class})
@EnableConfigurationProperties(UsuarioPath.class)
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UsuarioUseCase usuarioUseCase;

    private Usuario validUsuario;

    @BeforeEach
    void setUp() {
        validUsuario = Usuario.builder()
                .nombres("Jane")
                .apellidos("Doe")
                .fechaNacimiento(Date.from(LocalDate.of(1995, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .email("jane.doe@example.com")
                .documentoIdentidad("987654321")
                .telefono("0987654321")
                .salarioBase(new BigDecimal("6000000"))
                .idRol(1L)
                .nombreRol("Solicitante")
                .build();
    }

    @Test
    void shouldSaveUsuarioSuccessfully() {
        when(usuarioUseCase.saveUsuario(any(Usuario.class))).thenReturn(Mono.just(validUsuario));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuario)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Usuario.class)
                .isEqualTo(validUsuario);
    }

    @Test
    void shouldReturnBadRequestForBlankNombres() {
        validUsuario.setNombres("");
        when(usuarioUseCase.saveUsuario(any(Usuario.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("El nombre no puede estar vacío")));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuario)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("El nombre no puede estar vacío");
    }

    @Test
    void shouldReturnBadRequestForInvalidEmail() {
        validUsuario.setEmail("invalid-email");
        when(usuarioUseCase.saveUsuario(any(Usuario.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("El email no es válido")));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuario)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("El email no es válido");
    }

    @Test
    void shouldReturnBadRequestForExistingEmail() {
        when(usuarioUseCase.saveUsuario(any(Usuario.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("El correo electrónico ya está registrado")));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuario)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("El correo electrónico ya está registrado");
    }

    @Test
    void shouldReturnBadRequestForSalarioBaseOutOfRange() {
        validUsuario.setSalarioBase(new BigDecimal("20000000")); // Above max
        when(usuarioUseCase.saveUsuario(any(Usuario.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("El salario base debe estar entre 0 y 15,000,000")));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuario)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("El salario base debe estar entre 0 y 15,000,000");
    }

    @Test
    void shouldReturnBadRequestForFutureFechaNacimiento() {
        validUsuario.setFechaNacimiento(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        when(usuarioUseCase.saveUsuario(any(Usuario.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("La fecha de nacimiento no puede ser futura")));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuario)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("La fecha de nacimiento no puede ser futura");
    }

    // Add tests for other CRUD operations if needed, following the pattern
    @Test
    void shouldGetAllUsuarios() {
        Usuario anotherUser = validUsuario.toBuilder().email("another@example.com").build();
        when(usuarioUseCase.getAllUsuarios()).thenReturn(Flux.just(validUsuario, anotherUser));

        webTestClient.get()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Usuario.class)
                .hasSize(2)
                .contains(validUsuario, anotherUser);
    }

    @Test
    void shouldGetUsuarioById() {
        validUsuario.setId(1);
        when(usuarioUseCase.getUsuarioById(any(Long.class))).thenReturn(Mono.just(validUsuario));

        webTestClient.get()
                .uri("/api/v1/usuarios/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Usuario.class)
                .isEqualTo(validUsuario);
    }

    @Test
    void shouldUpdateUsuario() {
        Usuario updatedUsuario = validUsuario.toBuilder().id(1).nombres("Jane Updated").build();
        when(usuarioUseCase.updateUsuario(any(Usuario.class))).thenReturn(Mono.just(updatedUsuario));

        webTestClient.put()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedUsuario)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Usuario.class)
                .isEqualTo(updatedUsuario);
    }

    @Test
    void shouldDeleteUsuario() {
        when(usuarioUseCase.deleteUsuario(any(Long.class))).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/usuarios/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}