package co.com.crediya.model.usuario.gateways;

import co.com.crediya.model.usuario.Usuario;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface for the repository of usuarios.
 */
public interface UsuarioRepository {
    /**
     * Saves a usuario.
     *
     * @param usuario the usuario to save
     * @return a Mono containing the saved usuario
     */
    Mono<Usuario> save(Usuario usuario);

    /**
     * Gets all usuarios.
     *
     * @return a Flux containing all usuarios
     */
    Flux<Usuario> findAll();

    /**
     * Gets a usuario by its ID.
     *
     * @param id the ID of the usuario to get
     * @return a Mono containing the usuario
     */
    Mono<Usuario> findById(Long id);

    /**
     * Deletes a usuario by its ID.
     *
     * @param id the ID of the usuario to delete
     * @return a Mono that completes when the usuario is deleted
     */
    Mono<Void> deleteById(Long id);

    /**
     * Gets a usuario by its email.
     *
     * @param email the email of the usuario to get
     * @return a Mono containing the usuario
     */
    Mono<Usuario> findByEmail(String email);

    /**
     * Gets a usuario by its identity document.
     *
     * @param documentoIdentidad the identity document of the usuario to get
     * @return a Mono containing the usuario
     */
    Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad);
}

