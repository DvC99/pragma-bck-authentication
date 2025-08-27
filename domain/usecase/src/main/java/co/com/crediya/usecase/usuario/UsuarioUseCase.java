package co.com.crediya.usecase.usuario;

import co.com.crediya.model.exceptions.DocumentoIdentidadAlreadyExistsException;
import co.com.crediya.model.exceptions.EmailAlreadyExistsException;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.model.usuario.gateways.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Use case for managing usuarios.
 */
@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioGateway usuarioGateway;

    /**
     * Saves a new usuario after validating that the email and identity document are unique.
     *
     * @param usuario the usuario to save
     * @return a Mono containing the saved usuario, or an error if the email or document already exist.
     */
    public Mono<Usuario> saveUsuario(Usuario usuario) {
        return usuarioGateway.findByEmail(usuario.getEmail())
                .flatMap(existingUser -> Mono.error(new EmailAlreadyExistsException("El correo electrónico ya está registrado")))
                .switchIfEmpty(Mono.defer(() ->
                    usuarioGateway.findByDocumentoIdentidad(usuario.getDocumentoIdentidad())
                        .flatMap(existingUser -> Mono.error(new DocumentoIdentidadAlreadyExistsException("El documento de identidad ya está registrado")))
                        .switchIfEmpty(Mono.defer(() -> usuarioGateway.save(usuario)))
                ))
                .cast(Usuario.class);
    }

    /**
     * Updates an existing usuario after validating that the new email and identity document are unique.
     *
     * @param usuario the usuario to update
     * @return a Mono containing the updated usuario, or an error if the email or document are taken by another user.
     */
    public Mono<Usuario> updateUsuario(Usuario usuario) {
        Mono<Void> emailCheck = usuarioGateway.findByEmail(usuario.getEmail())
                .filter(existingUser -> !existingUser.getId().equals(usuario.getId()))
                .flatMap(existingUser -> Mono.error(new EmailAlreadyExistsException("El correo electrónico ya está registrado por otro usuario")));

        Mono<Void> documentCheck = usuarioGateway.findByDocumentoIdentidad(usuario.getDocumentoIdentidad())
                .filter(existingUser -> !existingUser.getId().equals(usuario.getId()))
                .flatMap(existingUser -> Mono.error(new DocumentoIdentidadAlreadyExistsException("El documento de identidad ya está registrado por otro usuario")));

        return Mono.when(emailCheck, documentCheck)
                .then(Mono.defer(() -> usuarioGateway.save(usuario)));
    }

    /**
     * Gets all usuarios.
     *
     * @return a Flux containing all usuarios
     */
    public Flux<Usuario> getAllUsuarios() {
        return usuarioGateway.findAll();
    }

    /**
     * Gets a usuario by its ID.
     *
     * @param id the ID of the usuario to get
     * @return a Mono containing the usuario
     */
    public Mono<Usuario> getUsuarioById(Long id) {
        return usuarioGateway.findById(id);
    }

    /**
     * Deletes a usuario by its ID.
     *
     * @param id the ID of the usuario to delete
     * @return a Mono that completes when the usuario is deleted
     */
    public Mono<Void> deleteUsuario(Long id) {
        return usuarioGateway.deleteById(id);
    }
}
