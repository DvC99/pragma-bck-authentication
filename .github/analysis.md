### Análisis Comparativo y Plan de Desarrollo para `bck-authentication`

Este documento presenta un análisis comparativo entre el proyecto de ejemplo `java_reactivo-main` y el proyecto actual `bck-authentication`, junto con un plan de desarrollo para cumplir con los requisitos del `enunciado_del_reto.txt`.

---

#### **Análisis General**

Ambos proyectos (`java_reactivo-main` y `bck-authentication`) siguen la misma estructura de Arquitectura Limpia y utilizan programación reactiva con Spring WebFlux y R2DBC. Esto es una excelente base, ya que se alinea perfectamente con los requisitos de tu proyecto de usar un modelo hexagonal y WebFlux.

**Similitudes Clave:**

*   **Arquitectura:** Ambos adhieren a la estructura de módulos de Arquitectura Limpia (application, domain/model, domain/usecase, infrastructure/driven-adapters, infrastructure/entry-points).
*   **Pila Reactiva:** Utilizan Spring Boot, Spring WebFlux, Project Reactor (`Mono`, `Flux`) y R2DBC para el acceso reactivo a la base de datos.
*   **Herramientas de Construcción:** Ambos usan Gradle con plugins similares (cleanArchitecture, Spring Boot, SonarQube, Jacoco, Pitest).
*   **Base de Datos:** Ambos se conectan a PostgreSQL con R2DBC.
*   **API:** Ambos exponen APIs REST utilizando Spring WebFlux Router Functions.
*   **Utilidades Comunes:** Comparten configuraciones como `ObjectMapperConfig` y `ReactiveAdapterOperations` para mapeo de datos y operaciones de repositorio comunes.

---

#### **Análisis Específico y Áreas de Desarrollo en `bck-authentication`**

Tu proyecto `bck-authentication` ya tiene una base sólida para la gestión de `Usuario` y `Rol`. Sin embargo, para cumplir con todos los requisitos del `enunciado_del_reto.txt`, hay varias funcionalidades y componentes que necesitan ser desarrollados o mejorados.

**1. Modelos de Dominio y Casos de Uso:**

*   **Actual:** Tienes los modelos `Usuario` y `Rol`, y un `UsuarioUseCase` funcional.
*   **Pendiente:** Necesitas crear los modelos y casos de uso para las siguientes funcionalidades:
    *   `Gestión de tipos de préstamos`
    *   `Proceso de solicitud` (incluyendo la `Capacidad de endeudamiento`)
    *   `Notificaciones automáticas`
    *   `Reportes de rendimiento`
*   **Acción:** Sigue el patrón existente de `Usuario` y `Rol` para definir las entidades de dominio y sus respectivos casos de uso en las capas `domain/model` y `domain/usecase`.

**2. Adaptadores Controlados (Driven Adapters):**

*   **Actual:** Tienes `r2dbc-postgresql` para la persistencia de `Usuario` y `Rol` en PostgreSQL.
*   **Pendiente:**
    *   **SQS para comunicación asíncrona:** El requisito de usar SQS para comunicación asíncrona implica crear un nuevo módulo de adaptador controlado (ej. `infrastructure/driven-adapters/sqs-adapter`) que interactúe con AWS SQS para enviar y/o recibir mensajes.
    *   **DynamoDB para reportes:** El requisito de usar DynamoDB para datos de reportes implica crear otro módulo de adaptador controlado (ej. `infrastructure/driven-adapters/dynamodb-adapter`) para la persistencia de datos no relacionales.
*   **Acción:** Desarrolla estos nuevos adaptadores siguiendo la estructura y principios de `r2dbc-postgresql`.

**3. Puntos de Entrada (Entry Points):**

*   **Actual:** Tienes `reactive-web` con endpoints para `Usuario`.
*   **Pendiente:** Crear los puntos de entrada REST para las nuevas funcionalidades (gestión de tipos de préstamos, solicitudes, etc.).
*   **Acción:** Extiende el módulo `reactive-web` añadiendo nuevas `Handler` y `RouterRest` para cada nueva entidad o funcionalidad, siguiendo el patrón de `Usuario`.

**4. Configuración:**

*   **Actual:** La configuración de la base de datos está en `application.yaml` apuntando a Supabase.
*   **Pendiente:** Añadir la configuración necesaria para SQS, DynamoDB y cualquier otro servicio de AWS que se integre.
*   **Acción:** Actualiza `application.yaml` y crea nuevas clases `@ConfigurationProperties` según sea necesario para gestionar estas configuraciones.

**5. Pruebas:**

*   **Actual:** Tienes pruebas básicas para la configuración, pero `UsuarioUseCaseTest` está vacío.
*   **Pendiente:** El `enunciado_del_reto.txt` enfatiza la necesidad de pruebas unitarias.
*   **Acción:** Escribe pruebas unitarias exhaustivas para todos los casos de uso y adaptadores controlados. También, considera añadir pruebas de integración para los puntos de entrada, similar a cómo se hace en el proyecto `java_reactivo-main`.

**6. Cumplimiento de Requisitos Adicionales del `enunciado_del_reto.txt`:**

*   **Swagger para documentación API:** Esto es una funcionalidad faltante. Necesitarás integrar una librería como SpringDoc OpenAPI en el módulo `reactive-web` para generar la documentación de tu API.
*   **Buenas prácticas de programación:** Continúa aplicando las buenas prácticas (nombramiento, manejo de constantes, métodos concisos) que ya se observan en el código existente.
*   **SonarLint:** El plugin de SonarQube ya está configurado en `build.gradle`, lo cual es un buen comienzo. Asegúrate de que las reglas de SonarLint se apliquen durante el desarrollo.
*   **Despliegue en AWS (ECS Fargate, API Gateway):** El `Dockerfile` ya existe, lo cual es un paso inicial. La configuración completa de despliegue en AWS es un proceso aparte que involucra la infraestructura.
*   **Docker y AWS ECR para CI/CD:** El `Dockerfile` es clave para esto. La integración con AWS ECR y el pipeline de CI/CD se configurarán en una etapa posterior.

---

#### **Hoja de Ruta / Próximos Pasos Sugeridos para `bck-authentication`:**

1.  **Completar y Fortalecer la Gestión de Usuarios y Roles:**
    *   **Prioridad Alta:** Escribe pruebas unitarias completas para `UsuarioUseCase` en `domain/usecase/src/test/java/co/com/crediya/usecase/usuario/UsuarioUseCaseTest.java`.
    *   **Prioridad Media:** Implementa el `RolUseCase` en `domain/usecase` (crear, actualizar, eliminar, buscar todos, buscar por ID).
    *   **Prioridad Media:** Extiende `infrastructure/entry-points/reactive-web` para añadir los endpoints REST para la gestión de `Rol`.

2.  **Integrar Documentación API (Swagger/OpenAPI):**
    *   **Prioridad Alta:** Añade la dependencia de SpringDoc OpenAPI al `build.gradle` del módulo `reactive-web` y configura la generación de la documentación.

3.  **Desarrollar la Gestión de Tipos de Préstamos:**
    *   **Prioridad Media:** Define el modelo `TipoPrestamo` en `domain/model`.
    *   **Prioridad Media:** Implementa el `TipoPrestamoUseCase` en `domain/usecase`.
    *   **Prioridad Media:** Crea el `TipoPrestamoRepository` en `infrastructure/driven-adapters/r2dbc-postgresql` para la persistencia.
    *   **Prioridad Media:** Añade los puntos de entrada REST para `TipoPrestamo` en `reactive-web`.

4.  **Planificar e Implementar el Proceso de Solicitud de Préstamos:**
    *   **Prioridad Media/Alta:** Define el modelo `SolicitudPrestamo` en `domain/model`.
    *   **Prioridad Alta:** Diseña e implementa el `SolicitudPrestamoUseCase`, que será el caso de uso central para la lógica de negocio de las solicitudes, incluyendo la evaluación automatizada y la interacción con el administrador.
    *   **Prioridad Media:** Crea el `SolicitudPrestamoRepository` para la persistencia.

5.  **Integrar Comunicación Asíncrona (SQS):**
    *   **Prioridad Media:** Crea un nuevo módulo `infrastructure/driven-adapters/sqs-adapter`.
    *   **Prioridad Media:** Implementa la lógica para enviar mensajes a SQS desde el `SolicitudPrestamoUseCase` (por ejemplo, para notificaciones).

6.  **Integrar Base de Datos No Relacional (DynamoDB) para Reportes:**
    *   **Prioridad Baja/Media:** Crea un nuevo módulo `infrastructure/driven-adapters/dynamodb-adapter`.
    *   **Prioridad Baja/Media:** Define los modelos de datos para los reportes y la lógica para persistirlos en DynamoDB.

7.  **Refinar el Manejo de Errores y la Validación:**
    *   **Prioridad Media:** Extiende `GlobalExceptionHandler` en `reactive-web` para manejar excepciones personalizadas de tus casos de uso, proporcionando respuestas de error claras.
    *   **Prioridad Media:** Utiliza las interfaces `ValidationGroup` para una validación más granular en tus modelos.

---

#### **Análisis Detallado y Resolución de Problemas (Iteración 1)**

Durante la implementación y prueba del microservicio `bck-authentication`, se encontraron y resolvieron varios problemas clave:

**1. Problema Inicial: Lógica de Validación en el DTO (`Usuario.java`)**
*   **Descripción:** Inicialmente, se intentó aplicar las anotaciones de validación de Jakarta Validation directamente en el modelo `Usuario.java`.
*   **Resolución:** A petición del usuario, se revirtió este cambio. La validación se trasladó al `UsuarioUseCase` para mantener la pureza del modelo de dominio y centralizar la lógica de negocio.

**2. Problema: Fallo de Pruebas de Integración (`RouterRestTest`) - `NoUniqueBeanDefinitionException`**
*   **Descripción:** Al ejecutar las pruebas de integración para `RouterRestTest`, Spring Boot lanzó un `NoUniqueBeanDefinitionException` para `UsuarioPath`. Esto ocurrió porque `UsuarioPath.class` fue incluido tanto en `@ContextConfiguration` como habilitado por `@EnableConfigurationProperties`, lo que llevó a la creación de dos beans.
*   **Resolución:** Se eliminó `UsuarioPath.class` de la lista de clases en `@ContextConfiguration` en `RouterRestTest.java`. `@EnableConfigurationProperties` es suficiente para que Spring gestione la inyección de propiedades.

**3. Problema: Fallo de Pruebas de Integración (`RouterRestTest`) - `IllegalArgumentException: 'pattern' must not be null`**
*   **Descripción:** Después de resolver el problema anterior, las pruebas de `RouterRestTest` fallaron porque `usuarioPath.getUsuarios()` devolvía `null`, lo que causaba un error al construir las rutas. Esto indicaba que las propiedades de `UsuarioPath` no se estaban cargando correctamente en el contexto de prueba.
*   **Resolución:** Se creó un archivo `application.yaml` en `bck-authentication/infrastructure/entry-points/reactive-web/src/test/resources/` y se incluyeron las propiedades `routes.paths.usuarios` y `routes.paths.usuariosById` con sus valores correctos. Esto aseguró que `UsuarioPath` se inicializara con los valores esperados durante las pruebas.

**4. Problema: Fallo de Pruebas de Integración (`RouterRestTest`) - `500 INTERNAL_SERVER_ERROR` en lugar de `400 Bad Request`**
*   **Descripción:** Las pruebas de validación en `RouterRestTest` esperaban un `400 Bad Request` con un mensaje de error específico, pero la aplicación devolvía un `500 INTERNAL_SERVER_ERROR` cuando `UsuarioUseCase` lanzaba un `IllegalArgumentException`. Esto indicaba que el `GlobalExceptionHandler` no estaba interceptando la excepción como se esperaba.
*   **Resolución:**
    *   Se añadió la anotación `@Order(Ordered.HIGHEST_PRECEDENCE)` a la clase `GlobalExceptionHandler` para asegurar que tuviera la máxima prioridad en el manejo de excepciones.
    *   Se añadió explícitamente `GlobalExceptionHandler.class` a la lista de clases en `@ContextConfiguration` en `RouterRestTest.java` para asegurar que fuera cargado en el contexto de prueba.
    *   Se añadió la importación `import co.com.crediya.api.handler.GlobalExceptionHandler;` en `RouterRestTest.java` para resolver el error de compilación.

**5. Problema: Fallo al Ejecutar la Aplicación Principal (`MainApplication`) - Error de Sintaxis en `application.yaml`**
*   **Descripción:** Al intentar ejecutar `MainApplication`, la aplicación falló debido a un error de sintaxis en `bck-authentication/applications/app-service/src/main/resources/application.yaml`, específicamente en la propiedad `cors.allowed-origins`, donde faltaba una comilla de cierre.
*   **Resolución:** Se añadió la comilla de cierre faltante en la línea correspondiente del `application.yaml`.

**6. Problema: Fallo al Ejecutar la Aplicación Principal (`MainApplication`) - Contexto de Ejecución de Gradle**
*   **Descripción:** Se encontraron dificultades para ejecutar la `MainApplication` usando `gradlew.bat` debido a problemas con el "directorio de trabajo registrado" y el contexto de ejecución de Gradle en un proyecto multi-módulo.
*   **Resolución:** La forma correcta de ejecutar la aplicación principal desde la raíz del proyecto multi-módulo es utilizando la ruta completa a `gradlew.bat` y especificando el subproyecto y la tarea: `C:\PROGRAMAS\pragma\crediya\gradlew.bat -p bck-authentication :app-service:co.com.crediya.MainApplication.main()`.

**Estado Actual del Microservicio `bck-authentication`:**
*   Todas las pruebas unitarias y de integración para la funcionalidad de registro de usuarios (`POST /api/v1/usuarios`) **han pasado exitosamente**.
*   La lógica de validación en `UsuarioUseCase` funciona correctamente.
*   El manejo de excepciones en `GlobalExceptionHandler` está configurado para devolver respuestas `400 Bad Request` con mensajes claros para errores de validación.
*   La aplicación principal (`MainApplication`) puede ser iniciada correctamente.

---