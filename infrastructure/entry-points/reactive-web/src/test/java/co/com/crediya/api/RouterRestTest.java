package co.com.crediya.api;

import co.com.crediya.api.config.UsuarioPath;
import co.com.crediya.api.dto.RolDTO;
import co.com.crediya.api.dto.UsuarioDTO;
import co.com.crediya.api.handler.GlobalExceptionHandler;
import co.com.crediya.api.mapper.UsuarioMapper;
import co.com.crediya.model.rol.Rol;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalExceptionHandler.class, UsuarioPath.class, UsuarioMapper.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private ApplicationContext context;

    private WebTestClient webTestClient;

    @MockitoBean
    private UsuarioUseCase usuarioUseCase;

    private UsuarioDTO validUsuarioDTO;
    private Usuario validUsuario;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();

        RolDTO rolDTO = RolDTO.builder().id(1).build();
        validUsuarioDTO = UsuarioDTO.builder()
                .nombres("Jane")
                .apellidos("Doe")
                .fechaNacimiento(Date.from(LocalDate.of(1995, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .email("jane.doe@example.com")
                .documentoIdentidad("987654321")
                .telefono("0987654321")
                .salarioBase(new BigDecimal("6000000"))
                .rol(rolDTO)
                .build();

        validUsuario = Usuario.builder()
                .nombres("Jane")
                .apellidos("Doe")
                .fechaNacimiento(Date.from(LocalDate.of(1995, 5, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .email("jane.doe@example.com")
                .documentoIdentidad("987654321")
                .telefono("0987654321")
                .salarioBase(new BigDecimal("6000000"))
                .rol(Rol.builder().id(1).build())
                .build();
    }

    @Test
    void shouldSaveUsuarioSuccessfully() {
        when(usuarioUseCase.saveUsuario(any(Usuario.class))).thenReturn(Mono.just(validUsuario));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuarioDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Usuario.class)
                .isEqualTo(validUsuario);
    }

    @Test
    void shouldReturnBadRequestForBlankNombres() {
        validUsuarioDTO.setNombres("");

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuarioDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.nombres").isEqualTo("El nombre no puede estar vacío");
    }

    @Test
    void shouldReturnBadRequestForInvalidEmail() {
        validUsuarioDTO.setEmail("invalid-email");

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuarioDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.email").isEqualTo("El email no es válido");
    }

    @Test
    void shouldReturnBadRequestForExistingEmail() {
        when(usuarioUseCase.saveUsuario(any(Usuario.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("El correo electrónico ya está registrado")));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuarioDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("El correo electrónico ya está registrado");
    }

    @Test
    void shouldReturnBadRequestForSalarioBaseOutOfRange() {
        validUsuarioDTO.setSalarioBase(new BigDecimal("20000000")); // Above max

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuarioDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.salarioBase").isEqualTo("El salario base debe ser como máximo 15,000,000");
    }

    @Test
    void shouldReturnBadRequestForFutureFechaNacimiento() {
        validUsuarioDTO.setFechaNacimiento(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuarioDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.fechaNacimiento").isEqualTo("La fecha de nacimiento no puede ser futura");
    }

    @Test
    void shouldReturnBadRequestForNullRol() {
        validUsuarioDTO.setRol(null);

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuarioDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.rol").isEqualTo("El rol del usuario no puede ser nulo");
    }

    @Test
    void shouldReturnBadRequestForNullRolId() {
        validUsuarioDTO.setRol(RolDTO.builder().id(null).build());

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuarioDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.['rol.id']").isEqualTo("El ID del rol no puede ser nulo");
    }

    @Test
    void shouldGetAllUsuarios() {
        when(usuarioUseCase.getAllUsuarios()).thenReturn(Flux.just(validUsuario));

        webTestClient.get()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Usuario.class).hasSize(1).contains(validUsuario);
    }

    @Test
    void shouldGetUsuarioById() {
        validUsuario.setId(1);
        when(usuarioUseCase.getUsuarioById(1L)).thenReturn(Mono.just(validUsuario));

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
        when(usuarioUseCase.updateUsuario(any(Usuario.class))).thenReturn(Mono.just(validUsuario));

        webTestClient.put()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUsuarioDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Usuario.class)
                .isEqualTo(validUsuario);
    }

    @Test
    void shouldDeleteUsuario() {
        when(usuarioUseCase.deleteUsuario(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/usuarios/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}