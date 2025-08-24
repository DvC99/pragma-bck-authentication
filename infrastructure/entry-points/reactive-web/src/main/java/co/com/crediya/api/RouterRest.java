package co.com.crediya.api;

import co.com.crediya.api.config.UsuarioPath;
import co.com.crediya.api.dto.UsuarioDTO;
import co.com.crediya.model.usuario.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final UsuarioPath usuarioPath;
    private final Handler usuarioHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/usuarios", produces = {APPLICATION_JSON_VALUE}, method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "listenSaveUsuario",
                    operation = @Operation(
                            summary = "Guardar un nuevo usuario",
                            description = "Permite registrar un nuevo usuario en el sistema.",
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario guardado exitosamente", content = @Content(schema = @Schema(implementation = Usuario.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
                            }
                    )),
            @RouterOperation(path = "/api/v1/usuarios", produces = {APPLICATION_JSON_VALUE}, method = RequestMethod.PUT, beanClass = Handler.class, beanMethod = "listenUpdateUsuario",
                    operation = @Operation(
                            summary = "Actualizar un usuario existente",
                            description = "Permite actualizar la información de un usuario existente.",
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(schema = @Schema(implementation = Usuario.class))),
                                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                            }
                    )),
            @RouterOperation(path = "/api/v1/usuarios/{id}", produces = {APPLICATION_JSON_VALUE}, method = RequestMethod.DELETE, beanClass = Handler.class, beanMethod = "listenDeleteUsuario",
                    operation = @Operation(
                            summary = "Eliminar un usuario por ID",
                            description = "Permite eliminar un usuario del sistema utilizando su ID.",
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID del usuario a eliminar", required = true, schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
                                    @ApiResponse(responseCode = "400", description = "ID de usuario inválido"),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                            }
                    )),
            @RouterOperation(path = "/api/v1/usuarios", produces = {APPLICATION_JSON_VALUE}, method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "listenGetAllUsuarios",
                    operation = @Operation(
                            summary = "Obtener todos los usuarios",
                            description = "Retorna una lista de todos los usuarios registrados en el sistema.",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Usuario.class))))
                            }
                    )),
            @RouterOperation(path = "/api/v1/usuarios/{id}", produces = {APPLICATION_JSON_VALUE}, method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "listenGetUsuarioById",
                    operation = @Operation(
                            summary = "Obtener un usuario por ID",
                            description = "Retorna un usuario específico utilizando su ID.",
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "id", description = "ID del usuario a obtener", required = true, schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente", content = @Content(schema = @Schema(implementation = Usuario.class))),
                                    @ApiResponse(responseCode = "400", description = "ID de usuario inválido"),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                            }
                    ))
    })
    public RouterFunction<ServerResponse> routerFunction() {
        return route(RequestPredicates.POST(usuarioPath.getUsuarios()), usuarioHandler::listenSaveUsuario)
                .andRoute(RequestPredicates.PUT(usuarioPath.getUsuarios()), usuarioHandler::listenUpdateUsuario)
                .andRoute(RequestPredicates.DELETE(usuarioPath.getUsuariosById()), usuarioHandler::listenDeleteUsuario)
                .andRoute(RequestPredicates.GET(usuarioPath.getUsuarios()), usuarioHandler::listenGetAllUsuarios)
                .andRoute(RequestPredicates.GET(usuarioPath.getUsuariosById()), usuarioHandler::listenGetUsuarioById);
    }
}
