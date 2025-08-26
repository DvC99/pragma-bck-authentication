package co.com.crediya.api.router.rol;

import co.com.crediya.api.handler.usuario.UsuarioHandler;
import co.com.crediya.api.dto.usuario.UsuarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Router for all Usuario related endpoints.
 */
@Configuration
@Tag(name = "Usuario", description = "Operaciones relacionadas con la gestión de usuarios")
public class UsuarioRouter {
    private static final String BASE_URL = "/api/v1/usuarios";

    /**
     * Route for saving a new usuario.
     *
     * @param usuarioHandler the handler for the request
     * @return a RouterFunction
     */
    @Bean
    @RouterOperation(path = BASE_URL, produces = {
            "application/json"}, method = RequestMethod.POST, beanClass = UsuarioHandler.class, beanMethod = "listenSaveUsuario",
            operation = @Operation(operationId = "listenSaveUsuario",
                    summary = "Guardar un nuevo usuario",
                    description = "Permite registrar un nuevo usuario en el sistema.",
                    requestBody = @RequestBody(required = true, description = "Datos del usuario a crear", content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
                    responses = {
                            @ApiResponse(responseCode = "200", description = "Usuario guardado exitosamente", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
                    }
            ))
    public RouterFunction<ServerResponse> saveUsuarioRoute(UsuarioHandler usuarioHandler) {
        return route(POST(BASE_URL).and(accept(APPLICATION_JSON)), usuarioHandler::listenSaveUsuario);
    }

    /**
     * Route for updating an existing usuario.
     *
     * @param usuarioHandler the handler for the request
     * @return a RouterFunction
     */
    @Bean
    @RouterOperation(path = BASE_URL, produces = {
            "application/json"}, method = RequestMethod.PUT, beanClass = UsuarioHandler.class, beanMethod = "listenUpdateUsuario",
            operation = @Operation(operationId = "listenUpdateUsuario",
                    summary = "Actualizar un usuario existente",
                    description = "Permite actualizar la información de un usuario existente.",
                    requestBody = @RequestBody(required = true, description = "Datos del usuario a actualizar", content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
                    responses = {
                            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
                    }
            ))
    public RouterFunction<ServerResponse> updateUsuarioRoute(UsuarioHandler usuarioHandler) {
        return route(PUT(BASE_URL).and(accept(APPLICATION_JSON)), usuarioHandler::listenUpdateUsuario);
    }

    /**
     * Route for getting a usuario by its ID.
     *
     * @param usuarioHandler the handler for the request
     * @return a RouterFunction
     */
    @Bean
    @RouterOperation(path = BASE_URL + "/{id}", produces = {
            "application/json"}, method = RequestMethod.GET, beanClass = UsuarioHandler.class, beanMethod = "listenGetUsuarioById",
            operation = @Operation(operationId = "listenGetUsuarioById",
                    summary = "Obtener un usuario por ID",
                    description = "Retorna un usuario específico utilizando su ID.",
                    parameters = {
                            @Parameter(in = ParameterIn.PATH, name = "id", description = "ID del usuario a obtener", required = true, schema = @Schema(type = "integer", format = "int64"))
                    },
                    responses = {
                            @ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                    }
            ))
    public RouterFunction<ServerResponse> getUsuarioByIdRoute(UsuarioHandler usuarioHandler) {
        return route(GET(BASE_URL + "/{id}"), usuarioHandler::listenGetUsuarioById);
    }

    /**
     * Route for getting all usuarios.
     *
     * @param usuarioHandler the handler for the request
     * @return a RouterFunction
     */
    @Bean
    @RouterOperation(path = BASE_URL, produces = {
            "application/json"}, method = RequestMethod.GET, beanClass = UsuarioHandler.class, beanMethod = "listenGetAllUsuarios",
            operation = @Operation(operationId = "listenGetAllUsuarios",
                    summary = "Obtener todos los usuarios",
                    description = "Retorna una lista de todos los usuarios registrados en el sistema.",
                    responses = {
                            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
                    }
            ))
    public RouterFunction<ServerResponse> getAllUsuariosRoute(UsuarioHandler usuarioHandler) {
        return route(GET(BASE_URL), usuarioHandler::listenGetAllUsuarios);
    }

    /**
     * Route for deleting a usuario by its ID.
     *
     * @param usuarioHandler the handler for the request
     * @return a RouterFunction
     */
    @Bean
    @RouterOperation(path = BASE_URL + "/{id}", produces = {
            "application/json"}, method = RequestMethod.DELETE, beanClass = UsuarioHandler.class, beanMethod = "listenDeleteUsuario",
            operation = @Operation(operationId = "listenDeleteUsuario",
                    summary = "Eliminar un usuario por ID",
                    description = "Permite eliminar un usuario del sistema utilizando su ID.",
                    parameters = {
                            @Parameter(in = ParameterIn.PATH, name = "id", description = "ID del usuario a eliminar", required = true, schema = @Schema(type = "integer", format = "int64"))
                    },
                    responses = {
                            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
                            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                    }
            ))
    public RouterFunction<ServerResponse> deleteUsuarioRoute(UsuarioHandler usuarioHandler) {
        return route(DELETE(BASE_URL + "/{id}"), usuarioHandler::listenDeleteUsuario);
    }
}