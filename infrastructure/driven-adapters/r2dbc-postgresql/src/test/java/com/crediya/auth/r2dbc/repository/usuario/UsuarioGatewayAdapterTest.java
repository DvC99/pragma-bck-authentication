package com.crediya.auth.r2dbc.repository.usuario;

import co.com.crediya.model.exceptions.RepositoryException;
import co.com.crediya.model.rol.gateways.RolGateway;
import co.com.crediya.model.usuario.Usuario;
import co.com.crediya.r2dbc.repository.usuario.UsuarioReactiveRepository;
import co.com.crediya.r2dbc.repository.usuario.UsuarioGatewayAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataAccessResourceFailureException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioGatewayAdapterTest {

    @Mock
    private UsuarioReactiveRepository reactiveRepository;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private RolGateway rolGateway;

    @InjectMocks
    private UsuarioGatewayAdapter adapter;

    @BeforeEach
    void setUp() {
        // No need to mock mapper.map in setUp for these tests as it's not directly involved in error wrapping
    }

    @Test
    @DisplayName("Debe envolver DataAccessException en RepositoryException para save")
    void saveShouldWrapException() {
        // Arrange
        when(reactiveRepository.save(any()))
                .thenReturn(Mono.error(new DataAccessResourceFailureException("Error de BD")));

        // Act
        Mono<Usuario> result = adapter.save(new Usuario());

        // Assert
        StepVerifier.create(result)
                .expectError(RepositoryException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe envolver DataAccessException en RepositoryException para findByEmail")
    void findByEmailShouldWrapException() {
        // Arrange
        when(reactiveRepository.findByEmail(anyString()))
                .thenReturn(Mono.error(new DataAccessResourceFailureException("Error de BD")));

        // Act
        Mono<Usuario> result = adapter.findByEmail("test@test.com");

        // Assert
        StepVerifier.create(result)
                .expectError(RepositoryException.class)
                .verify();
    }
}
