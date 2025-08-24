package co.com.crediya.api;

import co.com.crediya.api.dto.UsuarioDTO;
import co.com.crediya.api.mapper.UsuarioMapper;
import co.com.crediya.usecase.usuario.UsuarioUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Operaciones relacionadas con la gestión de usuarios")
@Slf4j
public class Handler {

    private final UsuarioUseCase usuarioUseCase;
    private final Validator validator;
    private final UsuarioMapper usuarioMapper;

    private <T> Mono<T> validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (violations.isEmpty()) {
            return Mono.just(object);
        }
        String errors = violations.stream()
                .map(v -> v.getPropertyPath().toString() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed for object {}: {}", object.getClass().getSimpleName(), errors);
        return Mono.error(new ConstraintViolationException(violations));
    }

    public Mono<ServerResponse> listenSaveUsuario(ServerRequest serverRequest) {
        log.info("Request received for listenSaveUsuario");
        return serverRequest.bodyToMono(UsuarioDTO.class)
                .doOnNext(dto -> log.debug("Request body: {}", dto))
                .flatMap(this::validate)
                .map(usuarioMapper::toModel)
                .flatMap(usuarioUseCase::saveUsuario)
                .flatMap(savedUsuario -> {
                    log.info("Successfully saved user with ID: {}", savedUsuario.getId());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(savedUsuario);
                })
                .doOnError(err -> log.error("Error processing save user request", err));
    }

    public Mono<ServerResponse> listenUpdateUsuario(ServerRequest serverRequest) {
        log.info("Request received for listenUpdateUsuario");
        return serverRequest.bodyToMono(UsuarioDTO.class)
                .doOnNext(dto -> log.debug("Request body: {}", dto))
                .flatMap(this::validate)
                .map(usuarioMapper::toModel)
                .flatMap(usuarioUseCase::updateUsuario)
                .flatMap(savedUsuario -> {
                    log.info("Successfully updated user with ID: {}", savedUsuario.getId());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(savedUsuario);
                })
                .doOnError(err -> log.error("Error processing update user request", err));
    }

    public Mono<ServerResponse> listenGetAllUsuarios(ServerRequest serverRequest) {
        log.info("Request received for listenGetAllUsuarios");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(usuarioUseCase.getAllUsuarios(), co.com.crediya.model.usuario.Usuario.class);
    }

    public Mono<ServerResponse> listenGetUsuarioById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        log.info("Request received for listenGetUsuarioById with ID: {}", id);
        return usuarioUseCase.getUsuarioById(Long.valueOf(id))
                .flatMap(usuario -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(usuario))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listenDeleteUsuario(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        log.info("Request received for listenDeleteUsuario with ID: {}", id);
        return usuarioUseCase.deleteUsuario(Long.valueOf(id))
                .then(ServerResponse.noContent().build());
    }
}