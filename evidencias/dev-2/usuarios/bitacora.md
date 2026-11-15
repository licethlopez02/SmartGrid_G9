# Bitácora: Microservicio (Usuarios, Dev 2)

| Paso | Tiempo estimado | Tiempo real | Fecha y hora (commit) |
|---|---|---|---|
| HU-05 · Alertas por correo | 60–90 min | — | 15-11-2026 |

La implementación agrega los consumidores Kafka de `anomalias.detectadas` y
`sensores.defectuosos`, el servicio de aplicación y el adaptador SMTP. Las credenciales se
leen de `MAIL_USERNAME` y `MAIL_PASSWORD`; no se almacenan en el repositorio. La prueba
unitaria `NotificacionAlertasServiceTest` valida los cuatro casos principales.
