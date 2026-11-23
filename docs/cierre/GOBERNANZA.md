# Gobernanza de la arquitectura de Smart Grid (Grupo 9)

## Guardarraíles de arquitectura

1. **Regla:** el dominio no importa clases de infraestructura.
   **Se verifica con:** el comando de PowerShell de la fitness function 1 de `ARQUITECTURA.md`.
   **Cuándo:** hoy, manualmente antes de cada Pull Request. Automatizable con una prueba ArchUnit en CI (propuesta, no implementada).
2. **Regla:** cada servicio usa su propia base de datos.
   **Se verifica con:** `Select-String -Path "*\src\main\resources\application*.y*ml" -Pattern "jdbc:postgresql"` y comparar las bases.
   **Cuándo:** hoy, manualmente al cambiar la configuración de un servicio. Automatizable como script en CI (propuesta).
3. **Regla:** los valores sensibles de los `application.yaml` (contraseñas y secretos) se leen con el formato `${VARIABLE:valor}`, de modo que se puedan sobrescribir con variables de entorno. La contraseña de la cuenta de correo desechable es una excepción registrada como deuda técnica.
   **Se verifica con:** buscar líneas con `password:` o `secret:` que no usen `${`; no debe haber coincidencias.
   **Cuándo:** hoy, manualmente antes de cada commit. Automatizable con un escáner de secretos en CI (propuesta).
4. **Regla:** la suite de cada servicio pasa antes de integrar a la rama principal.
   **Se verifica con:** `.\mvnw.cmd -q test` en cada servicio, con Docker abierto.
   **Cuándo:** antes de cada Pull Request.

## Cómo se toman las decisiones

- Decisiones locales (dentro de un módulo): las toma quien lo implementa y avisa en el canal del equipo.
- Decisiones que cruzan fronteras (eventos, APIs, bases de datos): se escribe un ADR y lo revisa el otro integrante antes de integrarlo.
- Las excepciones a una regla se registran como deuda técnica con un momento de revisión.

## Deuda técnica reconocida

| Deuda | Por qué se aceptó | Riesgo | Cuándo se revisa |
|---|---|---|---|
| Contraseña de aplicación de la cuenta de correo desechable escrita como valor por defecto en el `application.yaml` de usuarios-service | Para que el proyecto funcione al clonarlo, sin configurar nada | Cualquiera con acceso al repositorio puede enviar correos desde esa cuenta | Al terminar la entrega (revocar la contraseña) |
| Valores por defecto de desarrollo en los `application.yaml` (contraseñas de base de datos, secreto JWT, contraseña del operador) | Valen solo para el entorno local y se pueden sobrescribir con variables de entorno | Uso de credenciales conocidas fuera del entorno local; sobrescribir solo la variable `*_DB_PASSWORD` desincroniza al servicio del contenedor de Postgres | Antes de cualquier despliegue |
| gRPC entre servicios sin autenticación (`GrpcSecurityConfig`) | El proyecto corre solo en local y llegó hasta la comunicación entre microservicios | Acceso no autorizado si se exponen los puertos gRPC | Antes de cualquier despliegue |
| Alertas por correo sin reintentos, con entrega al menos una vez | Mantener la solución simple (ADR-005) | Correos duplicados o alertas no enviadas si usuarios-service está apagado o el SMTP falla | Si el proyecto continúa después de la defensa |
| Volúmenes con nombre para persistencia de las bases (resuelto) | Cada base de usuarios, medicion y facturacion usa un volumen con nombre declarado en su Compose | Los datos sobreviven a recrear el contenedor | Resuelto: verificado en los tres Compose |
| Sin Circuit Breaker en medicion-service | Solo se protege con el plazo de 5 s y la respuesta 503 | Lentitud de usuarios-service que se propaga a medicion | Si el proyecto continúa |
| Idempotencia de los consumidores Kafka no verificada | No se revisó en el alcance de esta etapa | Un duplicado puede repetir una acción | Si el proyecto continúa |

## Radar tecnológico

- **Adoptar:** PostgreSQL por servicio, gRPC para consultas, Outbox con Kafka para eventos y gateway con Eureka, porque ya sostienen los escenarios de calidad y están implementados.
- **Evaluar:** ArchUnit en CI para automatizar los guardarraíles, y Resilience4j en medicion-service, porque reducirían los riesgos 2 y 3 del ATAM.
- **Evitar por ahora:** CQRS, saga completa, service mesh y data mesh, porque no hay una medición ni una escala que los justifique (ADR-004 y sección 6 de `ARQUITECTURA.md`).