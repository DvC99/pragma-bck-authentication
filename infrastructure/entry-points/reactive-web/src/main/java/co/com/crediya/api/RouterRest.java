package co.com.crediya.api;

import co.com.crediya.api.config.UsuarioPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final UsuarioPath usuarioPath;
    private final Handler usuarioHandler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(usuarioPath.getUsuarios()), usuarioHandler::listenSaveUsuario)
                .andRoute(PUT(usuarioPath.getUsuarios()), usuarioHandler::listenUpdateUsuario)
                .andRoute(DELETE(usuarioPath.getUsuariosById()), usuarioHandler::listenDeleteUsuario)
                .andRoute(GET(usuarioPath.getUsuarios()), usuarioHandler::listenGetAllUsuarios)
                .andRoute(GET(usuarioPath.getUsuariosById()), usuarioHandler::listenGetUsuarioById);
    }
}