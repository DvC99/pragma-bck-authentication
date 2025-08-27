# Microservicio de Autenticación y Usuarios (bck-authentication)

Este microservicio es un componente de la plataforma **CrediYa** y su responsabilidad principal es la gestión completa de los usuarios del sistema, incluyendo su registro, actualización, consulta y eliminación.

Está desarrollado siguiendo los principios de la **Arquitectura Limpia (Hexagonal)**, utilizando un stack de programación reactiva con **Spring WebFlux** y **Project Reactor**.

---

## Requisitos Previos

- **Java 21** o superior.
- **Gradle 8.x** o superior.

---

## Cómo Empezar

### 1. Construir el Proyecto

Para compilar todo el código, ejecutar las pruebas y empaquetar la aplicación, utiliza el siguiente comando desde la raíz del directorio `bck-authentication`:

```bash
./gradlew build
```
o en Windows:
```bash
gradlew.bat build
```

### 2. Ejecutar la Aplicación

Puedes ejecutar la aplicación directamente usando el wrapper de Gradle. Esto iniciará el servidor en el puerto `8081` (configurado en `application.yaml`).

```bash
gradlew.bat -p bck-authentication :app-service:run
```

### 3. Ejecutar las Pruebas

Para ejecutar únicamente el conjunto de pruebas unitarias y de integración del proyecto, utiliza:

```bash
./gradlew test
```
o en Windows:
```bash
gradlew.bat test
```

---

## Documentación de la API (Swagger)

Una vez que la aplicación esté en ejecución, puedes acceder a la documentación interactiva de la API a través de Swagger UI.

- **URL de Swagger UI:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

Desde esta interfaz podrás ver todos los endpoints disponibles, sus parámetros, cuerpos de petición y respuestas esperadas, además de poder probar la API directamente.

---

## Arquitectura

El proyecto sigue los principios de la Arquitectura Limpia para asegurar una alta mantenibilidad y un bajo acoplamiento entre la lógica de negocio y los detalles de infraestructura.

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

### Domain

Es el módulo más interno, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio. No tiene dependencias de ningún framework externo.
- **`model`**: Contiene los objetos de dominio puros (ej. `Usuario`) y las interfaces de los gateways (puertos).
- **`usecase`**: Contiene los casos de uso que orquestan las reglas de negocio.

### Infrastructure

Contiene los detalles técnicos y puntos de contacto con el mundo exterior.
- **`entry-points`**: Adaptadores que manejan las peticiones entrantes.
  - **`reactive-web`**: Implementa la API REST reactiva con Spring WebFlux, incluyendo los Routers, Handlers y DTOs.
- **`driven-adapters`**: Adaptadores que se comunican con servicios externos.
  - **`r2dbc-postgresql`**: Implementación del gateway de persistencia para comunicarse con la base de datos PostgreSQL de forma reactiva.

### Application

Este módulo es el más externo, responsable de ensamblar la aplicación. Resuelve la inyección de dependencias, configura los beans de Spring y contiene la clase principal (`MainApplication`) para arrancar el servicio.