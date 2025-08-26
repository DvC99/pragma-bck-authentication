package co.com.crediya.model.rol.gateways;

import co.com.crediya.model.rol.Rol;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface for the repository of roles.
 */
public interface RolGateway {
    /**
     * Saves a rol.
     *
     * @param rol the rol to save
     * @return a Mono containing the saved rol
     */
    Mono<Rol> save(Rol rol);

    /**
     * Gets all roles.
     *
     * @return a Flux containing all roles
     */
    Flux<Rol> findAll();

    /**
     * Gets a rol by its ID.
     *
     * @param id the ID of the rol to get
     * @return a Mono containing the rol
     */
    Mono<Rol> findById(Long id);

    /**
     * Deletes a rol by its ID.
     *
     * @param id the ID of the rol to delete
     * @return a Mono that completes when the rol is deleted
     */
    Mono<Void> deleteById(Long id);
}

