package co.com.crediya.api.handler.usuario;

import co.com.crediya.api.dto.ApiResponse;
import co.com.crediya.api.dto.usuario.UsuarioDTO;
import co.com.crediya.api.mapper.usuario.UsuarioMapper;
import co.com.crediya.api.validator.RequestValidator;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.usecase.usuario.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Handler for requests related to Usuarios.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioHandler {
    private final UsuarioUseCase usuarioUseCase;
    private final RequestValidator requestValidator;
    private final UsuarioMapper usuarioMapper;
    private final TransactionalOperator transactionalOperator;

    /**
     * Handles the request to save a new usuario.
     *
     * @param serverRequest the server request
     * @return a Mono containing the server response
     */
    public Mono<ServerResponse> listenSaveUsuario(ServerRequest serverRequest) {
        log.info("Request received for listenSaveUsuario");

        Mono<Usuario> saveFlow = serverRequest.bodyToMono(UsuarioDTO.class)
                .doOnNext(dto -> log.debug("Request body: {}", dto))
                .flatMap(requestValidator::validate)
                .map(usuarioMapper::toModel)
                .flatMap(usuarioUseCase::saveUsuario);

        return transactionalOperator.transactional(saveFlow)
                .flatMap(savedUsuario -> {
                    log.info("Successfully saved user with ID: {}", savedUsuario.getId());
                    ApiResponse<Usuario> apiResponse = ApiResponse.<Usuario>builder()
                            .codigo(HttpStatus.OK.value())
                            .mensaje("Usuario guardado exitosamente")
                            .body(savedUsuario)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(apiResponse);
                })
                .doOnError(err -> log.error("Error processing save user request", err));
    }

    /**
     * Handles the request to update an existing usuario.
     *
     * @param serverRequest the server request
     * @return a Mono containing the server response
     */
    public Mono<ServerResponse> listenUpdateUsuario(ServerRequest serverRequest) {
        log.info("Request received for listenUpdateUsuario");

        Mono<Usuario> updateFlow = serverRequest.bodyToMono(UsuarioDTO.class)
                .doOnNext(dto -> log.debug("Request body: {}", dto))
                .flatMap(requestValidator::validate)
                .map(usuarioMapper::toModel)
                .flatMap(usuarioUseCase::updateUsuario);

        return transactionalOperator.transactional(updateFlow)
                .flatMap(savedUsuario -> {
                    log.info("Successfully updated user with ID: {}", savedUsuario.getId());
                    ApiResponse<Usuario> apiResponse = ApiResponse.<Usuario>builder()
                            .codigo(HttpStatus.OK.value())
                            .mensaje("Usuario actualizado exitosamente")
                            .body(savedUsuario)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(apiResponse);
                })
                .doOnError(err -> log.error("Error processing update user request", err));
    }

    /**
     * Handles the request to get all usuarios.
     *
     * @param serverRequest the server request
     * @return a Mono containing the server response
     */
    public Mono<ServerResponse> listenGetAllUsuarios(ServerRequest serverRequest) {
        log.info("Request received for listenGetAllUsuarios");
        return usuarioUseCase.getAllUsuarios().collectList()
                .flatMap(usuarios -> {
                    ApiResponse<Object> apiResponse = ApiResponse.builder()
                            .codigo(HttpStatus.OK.value())
                            .mensaje("Usuarios obtenidos exitosamente")
                            .body(usuarios)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(apiResponse);
                });
    }

    /**
     * Handles the request to get a usuario by its ID.
     *
     * @param serverRequest the server request
     * @return a Mono containing the server response
     */
    public Mono<ServerResponse> listenGetUsuarioById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        log.info("Request received for listenGetUsuarioById with ID: {}", id);
        return usuarioUseCase.getUsuarioById(Long.valueOf(id))
                .flatMap(usuario -> {
                    ApiResponse<Usuario> apiResponse = ApiResponse.<Usuario>builder()
                            .codigo(HttpStatus.OK.value())
                            .mensaje("Usuario obtenido exitosamente")
                            .body(usuario)
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(apiResponse);
                })
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.<Void>builder()
                                .codigo(HttpStatus.NOT_FOUND.value())
                                .mensaje("No se encontraron datos para el ID proporcionado: " + id)
                                .build()));
    }

    /**
     * Handles the request to delete a usuario by its ID.
     *
     * @param serverRequest the server request
     * @return a Mono containing the server response
     */
    public Mono<ServerResponse> listenDeleteUsuario(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        log.info("Request received for listenDeleteUsuario with ID: {}", id);
        return usuarioUseCase.deleteUsuario(Long.valueOf(id))
                .then(ServerResponse.noContent().build());
    }
}
