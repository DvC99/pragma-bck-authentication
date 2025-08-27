### Análisis Técnico y Bitácora de Desarrollo para `bck-authentication`

Este documento presenta un análisis comparativo del proyecto, las decisiones arquitectónicas tomadas, y el proceso de implementación y corrección de funcionalidades clave.

---

### Resumen de Implementación y Correcciones

El objetivo de esta iteración fue solucionar dos problemas principales en el microservicio `bck-authentication`: la documentación de la API con Swagger que no se generaba correctamente y la ausencia de un registro de logs para observabilidad. Adicionalmente, se abordó la refactorización del sistema de validaciones para mejorar la calidad del código.

#### Proceso de Desarrollo y Desafíos

1.  **Análisis de Validaciones:** Se identificó que la lógica de validación manual en `UsuarioUseCase` aumentaba la complejidad del código y era un candidato a ser marcado por SonarLint. Se propuso mover las validaciones al modelo de dominio usando anotaciones de Jakarta Validation.

2.  **Fallo por Regla de Arquitectura:** El intento de añadir la dependencia de validación en el `domain/model` provocó un fallo en la tarea de Gradle `:validateStructure`. La investigación reveló una estricta regla de negocio que prohíbe dependencias externas en la capa del modelo para garantizar su pureza.

3.  **Adopción del Patrón DTO:** Tras el fallo, se adoptó el patrón **Data Transfer Object (DTO)** como la solución arquitectónicamente correcta. Se crearon `UsuarioDTO` y `RolDTO` en la capa de infraestructura (`reactive-web`) para manejar el contrato con el exterior y las anotaciones de validación.

4.  **Ajuste de Pruebas:** La refactorización obligó a una corrección integral de las pruebas unitarias y de integración. Se añadió la dependencia de validación al entorno de pruebas de `reactive-web`, se eliminaron las pruebas de validación obsoletas en `usecase` y se actualizaron las pruebas de `RouterRestTest` para que utilizaran los DTOs y verificaran los nuevos mensajes de error.

5.  **Diagnóstico Final de Swagger:** A pesar de todas las correcciones, la UI de Swagger seguía sin mostrar los detalles de los endpoints. Se concluyó que la causa probable era una limitación o bug en la forma en que `springdoc` procesa un único Bean de `RouterFunction` que agrupa múltiples rutas.

#### Soluciones Finales Implementadas

*   **Sistema de Validación:** El proyecto ahora cuenta con un sistema de validación robusto y declarativo basado en anotaciones sobre los DTOs, cumpliendo al 100% con las restricciones de la arquitectura.
*   **Documentación de API (Swagger):** Se refactorizó `RouterRest.java` para declarar un `@Bean` de `RouterFunction` por cada ruta individual. Cada bean fue anotado con su `@RouterOperation` correspondiente, eliminando la ambigüedad y forzando a `springdoc` a procesar la metadata correctamente.
*   **Registro de Logs:** Se implementó logging con SLF4J en la capa de infraestructura (`Handler.java`), proporcionando visibilidad sobre las peticiones y errores sin "contaminar" las capas de dominio, tal como lo exige la arquitectura del proyecto.

**Estado Final:** El proyecto se encuentra en un estado estable, con una build exitosa (`BUILD SUCCESSFUL`), todas las pruebas pasando y los requisitos iniciales completamente implementados.

---

### Decisión Arquitectónica Clave: Validación y el Patrón DTO

Durante el desarrollo, surgió una pregunta fundamental sobre la estrategia de validación de datos de entrada, la cual está directamente ligada a los principios de la Arquitectura Limpia que rigen el proyecto.

*   **Modelos de Dominio (Ej: `Usuario` en `domain/model`):** Son el **corazón de la aplicación**. Representan los conceptos y las reglas de negocio fundamentales. Su característica principal es la **pureza**: no tienen dependencias de frameworks externos (web, base de datos, validación). Solo cambian cuando las reglas del negocio cambian.

*   **DTOs (Ej: `UsuarioDTO` en `infrastructure/entry-points`):** Son clases diseñadas específicamente para **transferir datos** en los límites de la aplicación (la API REST). Son el **contrato público** con el mundo exterior y es aquí donde se colocan las anotaciones de frameworks (`@NotBlank`, `@JsonProperty`, `@Schema` de Swagger, etc.).

Esta separación es la esencia de la Arquitectura Limpia, garantiza el bajo acoplamiento y cumple con las reglas de validación del proyecto.

---

#### **Análisis Detallado y Resolución de Problemas**

Durante la implementación y prueba del microservicio, se encontraron y resolvieron varios problemas clave:

**1. Problema: Estrategia de Validación y Cumplimiento de la Arquitectura**
*   **Descripción:** Se necesitaba una estrategia de validación que no generara advertencias de complejidad en SonarLint y que, al mismo tiempo, cumpliera con la regla de "cero dependencias" en el modelo de dominio impuesta por el validador de arquitectura de Gradle.
*   **Resolución:** Se implementó el **patrón DTO**. Se crearon `UsuarioDTO` y `RolDTO` en la capa de `infrastructure/entry-points/reactive-web` para manejar las anotaciones de validación de Jakarta. El `Handler` ahora recibe estos DTOs, los valida, y solo después los mapea a los modelos de dominio puros antes de invocar al `UsuarioUseCase`. Esto resolvió tanto la restricción de la arquitectura como el problema de la complejidad del código.

**2. Problema: Fallo de Pruebas de Integración (`RouterRestTest`) - `NoUniqueBeanDefinitionException`**
*   **Descripción:** Al ejecutar las pruebas de integración, Spring Boot lanzaba `NoUniqueBeanDefinitionException` para `UsuarioPath` debido a una doble registración del bean.
*   **Resolución:** Se eliminó `UsuarioPath.class` de la anotación `@ContextConfiguration` en `RouterRestTest.java`, ya que `@EnableConfigurationProperties` era suficiente.

**3. Problema: Fallo de Pruebas de Integración (`RouterRestTest`) - `IllegalArgumentException: 'pattern' must not be null`**
*   **Descripción:** Las pruebas fallaban porque las propiedades de configuración de rutas no se cargaban en el contexto de prueba, resultando en un `null`.
*   **Resolución:** Se creó un archivo `application.yaml` en la ruta de recursos de prueba (`src/test/resources`) con las propiedades de las rutas necesarias para las pruebas.

**4. Problema: Fallo de Pruebas de Integración (`RouterRestTest`) - `500 INTERNAL_SERVER_ERROR` en lugar de `400 Bad Request`**
*   **Descripción:** El `GlobalExceptionHandler` no estaba interceptando las excepciones de validación del caso de uso con la prioridad correcta.
*   **Resolución:** Se añadió la anotación `@Order(Ordered.HIGHEST_PRECEDENCE)` al `GlobalExceptionHandler` y se registró explícitamente en el `@ContextConfiguration` de la clase de prueba.

**5. Problema: Fallo al Ejecutar la Aplicación (`MainApplication`) - Error de Sintaxis en `application.yaml`**
*   **Descripción:** Una comilla de cierre faltante en la propiedad `cors.allowed-origins` impedía que la aplicación se iniciara.
*   **Resolución:** Se corrigió el error de sintaxis en el archivo `application.yaml`.

**6. Problema: Fallo al Ejecutar la Aplicación (`MainApplication`) - Contexto de Ejecución de Gradle**
*   **Descripción:** Dificultades para ejecutar la aplicación desde la raíz de un proyecto multi-módulo.
*   **Resolución:** Se estableció que la forma correcta de ejecutar la aplicación es usando el flag `-p` de Gradle para especificar el subproyecto: `gradlew.bat -p bck-authentication :app-service:run`.

**7. Problema: Documentación Swagger Incompleta a Pesar de Anotaciones Correctas**
*   **Descripción:** Tras implementar el patrón DTO y corregir las configuraciones básicas, la UI de Swagger mostraba los endpoints pero sin ningún detalle (parámetros, cuerpos de petición, descripciones), a pesar de que las anotaciones `@RouterOperation` eran correctas.
*   **Causa Raíz:** La hipótesis final fue que `springdoc` presenta dificultades para procesar un único `@Bean` de `RouterFunction` que encadena múltiples rutas con el método `.andRoute()`.
*   **Resolución:** Se refactorizó la clase `RouterRest.java` para declarar un `@Bean` de `RouterFunction` para cada ruta individual. Cada bean se anotó con su propia `@RouterOperation`, creando una asociación directa e inequívoca entre la ruta y su documentación, lo que finalmente solucionó el problema de procesamiento.

**8. Problema: Error de Compilación en `RouterRest.java`**
*   **Descripción:** Después de refactorizar `RouterRest.java` para separar los beans, se produjo un error de compilación (`cannot find symbol`) para la constante `APPLICATION_JSON_VALUE`.
*   **Causa Raíz:** Durante el refactor se eliminó la importación estática necesaria para dicha constante.
*   **Resolución:** Se modificó el código para utilizar el literal de texto `"application/json"` directamente en el atributo `produces` de las anotaciones, lo cual es una práctica más robusta que evita problemas de importación.

---

### Refactorización Avanzada y Estrategia de Errores (Iteración 2)

En esta segunda iteración, el enfoque fue robustecer la arquitectura, mejorar la manenibilidad y estandarizar la comunicación de la API.

#### Resumen de Mejoras Implementadas

1.  **Centralización de la Validación:** Se extrajo la lógica de validación de los DTOs a un componente genérico y reutilizable (`RequestValidator`), eliminando la duplicación de código en los Handlers y centralizando esta responsabilidad de la capa de infraestructura.

2.  **Modernización de DTOs a Records:** Las clases `UsuarioDTO` y `RolDTO` fueron refactorizadas para usar `records` de Java. Este cambio reduce significativamente el código boilerplate (generado por Lombok) y garantiza la inmutabilidad de los objetos de transferencia, alineándose con las prácticas modernas de Java.

3.  **Estandarización de la Respuesta de la API:** Se implementó una estructura de respuesta JSON genérica (`ApiResponse<T>`) con los campos `codigo`, `mensaje` y `body`. Todos los endpoints y manejadores de errores fueron actualizados para usar este formato, asegurando una comunicación predecible y consistente con los clientes de la API.

4.  **Modularización de Entry Points:** Para facilitar la escalabilidad y la adición de nuevas entidades en el futuro, se renombraron las clases `Handler` y `RouterRest` a `UsuarioHandler` y `UsuarioRouter` respectivamente, estableciendo un patrón de un handler/router por entidad.

5.  **Segregación de Errores por Capa Arquitectónica:** Se implementó una estrategia de manejo de excepciones híbrida para segregar los errores según su capa de origen:
    *   **Excepciones de Dominio:** Se creó una clase base `DomainException` y excepciones específicas como `EmailAlreadyExistsException`. Los casos de uso ahora lanzan estos errores de negocio descriptivos, permitiendo al `GlobalExceptionHandler` mapearlos a códigos de estado HTTP precisos (ej. `409 Conflict`).
    *   **Excepciones de Infraestructura:** Se introdujo una `InfrastructureException` y una `RepositoryException`. Los adaptadores de base de datos ahora encapsulan las excepciones de bajo nivel (como `DataAccessException`) en estas excepciones personalizadas. Esto cumple con el principio de la Arquitectura Limpia de no filtrar detalles de la infraestructura hacia las capas internas.
    *   **Manejo Centralizado:** El `GlobalExceptionHandler` fue mejorado para identificar el tipo de excepción (Validación, Dominio, Infraestructura) y devolver la `ApiResponse` con el código HTTP (400, 409, 500) y el mensaje adecuado para cada caso.

6.  **Documentación Exhaustiva (Javadoc):** Se añadió documentación Javadoc completa a todas las clases modificadas y nuevas, mejorando la claridad del código y facilitando su mantenimiento futuro.

**Estado Final de la Iteración:** El proyecto cuenta ahora con una base de código más robusta, mantenible y escalable. La estandarización de respuestas y el manejo de errores por capas fortalecen la API, haciéndola más predecible y fácil de consumir.

---

### Iteración 3: Lógica de Negocio Adicional y Claridad Arquitectónica

Esta iteración se centró en expandir las reglas de negocio y solidificar los conceptos arquitectónicos del proyecto.

#### Resumen de Mejoras Implementadas

1.  **Validación de Unicidad Extendida:** Se implementó una nueva regla de negocio crítica: tanto el `email` como el `documentoIdentidad` deben ser únicos para cada usuario.
    *   La lógica se añadió en el `UsuarioUseCase` para los métodos `saveUsuario` y `updateUsuario`.
    *   La validación para la actualización (`updateUsuario`) se implementó cuidadosamente para asegurar que un campo no sea reportado como duplicado si pertenece al mismo usuario que se está modificando.

2.  **Ampliación de la Capa de Dominio:**
    *   Para soportar la nueva regla, se extendió el gateway `UsuarioRepository` con un nuevo método: `findByDocumentoIdentidad`.
    *   Se implementó el método correspondiente en el `UsuarioRepositoryAdapter` y en la capa de persistencia de Spring Data.

3.  **Manejo de Errores Más Específico:**
    *   Se creó una nueva excepción de dominio, `DocumentoIdentidadAlreadyExistsException`, para comunicar de forma precisa la violación de esta nueva regla de negocio.
    *   El `GlobalExceptionHandler` se actualizó para manejar esta excepción de forma dedicada, devolviendo un código de estado `409 Conflict`, lo que proporciona una retroalimentación mucho más clara al cliente de la API.

4.  **Análisis y Clarificación de Patrones Arquitectónicos:**
    *   Se analizó y documentó la relación entre los patrones **Gateway** y **Repository** en el proyecto.
    *   Se concluyó que el proyecto utiliza correctamente el Patrón Repository como una especialización del Patrón Gateway, donde la interfaz `UsuarioRepository` actúa como el puerto del dominio.
    *   Se reafirmó el flujo correcto de dependencias: `Adaptador Driving (Handler)` -> `Caso de Uso` -> `Interfaz Gateway (Repository)` <- `Adaptador Driven (Implementación del Repository)`, clarificando que un adaptador driven (como un repositorio) nunca debe invocar a un caso de uso.
