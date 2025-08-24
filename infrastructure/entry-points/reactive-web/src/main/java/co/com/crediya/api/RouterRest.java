package co.com.crediya.api;

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
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperation(path = "/api/v1/usuarios", produces = {
            "application/json"}, method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "listenSaveUsuario",
            operation = @Operation(operationId = "listenSaveUsuario", tags = "Usuario",
                    summary = "Guardar un nuevo usuario",
                    description = "Permite registrar un nuevo usuario en el sistema.",
                    requestBody = @RequestBody(required = true, description = "Datos del usuario a crear", content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
                    responses = {
                            @ApiResponse(responseCode = "200", description = "Usuario guardado exitosamente", content = @Content(schema = @Schema(implementation = Usuario.class))),
                            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
                    }
            ))
    public RouterFunction<ServerResponse> saveUsuarioRoute(Handler handler) {
        return route(POST("/api/v1/usuarios").and(accept(APPLICATION_JSON)), handler::listenSaveUsuario);
    }

    @Bean
    @RouterOperation(path = "/api/v1/usuarios", produces = {
            "application/json"}, method = RequestMethod.PUT, beanClass = Handler.class, beanMethod = "listenUpdateUsuario",
            operation = @Operation(operationId = "listenUpdateUsuario", tags = "Usuario",
                    summary = "Actualizar un usuario existente",
                    description = "Permite actualizar la información de un usuario existente.",
                    requestBody = @RequestBody(required = true, description = "Datos del usuario a actualizar", content = @Content(schema = @Schema(implementation = UsuarioDTO.class))),
                    responses = {
                            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(schema = @Schema(implementation = Usuario.class))),
                            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
                    }
            ))
    public RouterFunction<ServerResponse> updateUsuarioRoute(Handler handler) {
        return route(PUT("/api/v1/usuarios").and(accept(APPLICATION_JSON)), handler::listenUpdateUsuario);
    }

    @Bean
    @RouterOperation(path = "/api/v1/usuarios/{id}", produces = {
            "application/json"}, method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "listenGetUsuarioById",
            operation = @Operation(operationId = "listenGetUsuarioById", tags = "Usuario",
                    summary = "Obtener un usuario por ID",
                    description = "Retorna un usuario específico utilizando su ID.",
                    parameters = {
                            @Parameter(in = ParameterIn.PATH, name = "id", description = "ID del usuario a obtener", required = true, schema = @Schema(type = "integer", format = "int64"))
                    },
                    responses = {
                            @ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente", content = @Content(schema = @Schema(implementation = Usuario.class))),
                            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
                    }
            ))
    public RouterFunction<ServerResponse> getUsuarioByIdRoute(Handler handler) {
        return route(GET("/api/v1/usuarios/{id}"), handler::listenGetUsuarioById);
    }

    @Bean
    @RouterOperation(path = "/api/v1/usuarios", produces = {
            "application/json"}, method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "listenGetAllUsuarios",
            operation = @Operation(operationId = "listenGetAllUsuarios", tags = "Usuario",
                    summary = "Obtener todos los usuarios",
                    description = "Retorna una lista de todos los usuarios registrados en el sistema.",
                    responses = {
                            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Usuario.class))))
                    }
            ))
    public RouterFunction<ServerResponse> getAllUsuariosRoute(Handler handler) {
        return route(GET("/api/v1/usuarios"), handler::listenGetAllUsuarios);
    }

    @Bean
    @RouterOperation(path = "/api/v1/usuarios/{id}", produces = {
            "application/json"}, method = RequestMethod.DELETE, beanClass = Handler.class, beanMethod = "listenDeleteUsuario",
            operation = @Operation(operationId = "listenDeleteUsuario", tags = "Usuario",
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
    public RouterFunction<ServerResponse> deleteUsuarioRoute(Handler handler) {
        return route(DELETE("/api/v1/usuarios/{id}"), handler::listenDeleteUsuario);
    }
}