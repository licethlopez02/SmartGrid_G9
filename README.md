# Smart Grid G9

Sistema para recibir lecturas de medidores inteligentes de consumo eléctrico, detectar anomalías, avisar al usuario por correo y generar la facturación periódica por consumo acumulado. Proyecto de Ingeniería de Software II, UPTC.

- **Backend (este repositorio):** microservicios Spring Boot (usuarios, medición, facturación), API Gateway, Eureka, Kafka y un PostgreSQL por servicio.
- **Frontend:** repositorio aparte, `Smart-Grid-Frontend` (React + Vite + TypeScript).
- **Arquitectura y decisiones:** [`ARQUITECTURA.md`](ARQUITECTURA.md).

## Historias de usuario cubiertas

| HU | Qué hace | Dónde se ve |
| --- | --- | --- |
| HU-01 | El operador registra un medidor y lo asocia a un usuario | Frontend, "Registrar medidor" (operador) |
| HU-02 | El medidor envía lecturas | `POST /api/medidores/{id}/lecturas` (simulador en el repositorio del frontend) |
| HU-03 | Consumo acumulado por período | `GET /api/medidores/{id}/consumo` |
| HU-04 | Detección de anomalías por umbral relativo al histórico del medidor | Se genera al recibir cada lectura |
| HU-05 | El usuario recibe una alerta de anomalía | Pantalla "Alertas" y correo electrónico |
| HU-06 | Factura periódica con consumo acumulado y tarifa vigente | Automática el día 1 de cada mes, 02:00 (hora de Bogotá) |
| HU-07 | El usuario consulta su historial de consumo | Frontend, "Consumo" |
| HU-08 | El usuario consulta el estado de sus facturas | Frontend, "Facturas" |
| HU-09 | El operador ve el consumo agregado por zona | Frontend, "Consumo por zona" (operador) |

Reglas de negocio: una lectura negativa se descarta y genera una alerta de sensor defectuoso (R1); la factura solo se genera con el período completo (R2); las anomalías no resueltas se incluyen en la factura (R3); un medidor no puede asociarse a dos usuarios (R4).

## Requisitos previos

| Herramienta | Versión | Para qué |
| --- | --- | --- |
| Java (JDK) | 25 | Compilar y ejecutar los servicios |
| Maven | No hace falta instalarlo: cada módulo trae `mvnw.cmd` | Compilar y ejecutar |
| Docker Desktop | Abierto y en ejecución | Bases de datos y Kafka |
| Node.js | 18 o superior | Frontend y simulador de medidor |
| Cuenta de Gmail (opcional) | — | Solo si quieres enviar las alertas desde tu propia cuenta; el proyecto trae una cuenta de desarrollo ya configurada |

Los módulos usan Spring Boot 4.1.2.

Comprobar:

```powershell
java -version
docker version
node -v
```

## Estructura del repositorio

```text
SmartGrid_G9/
├── infra/                  Kafka (docker-compose.yaml)
├── eureka-server/          Registro de servicios
├── gateway/                API Gateway
├── usuarios-service/       Cuentas, login y alertas por correo
├── medicion-service/       Medidores, lecturas, anomalías y consumo
├── facturacion-service/    Tarifas y facturas
├── bruno/                  Colección de pruebas de la API
├── evidencias/             Evidencias del desarrollo
└── ARQUITECTURA.md
```

El frontend vive en otro repositorio: `Smart-Grid-Frontend/`.

## Puertos

| Componente | Puerto |
| --- | --- |
| Frontend (Vite) | 5173 |
| API Gateway | 8000 |
| usuarios-service (HTTP / gRPC) | 8083 / 9091 |
| medicion-service (HTTP / gRPC) | 8080 / 9090 |
| facturacion-service (HTTP) | 8081 (no expone gRPC; es cliente de medición en el 9090) |
| Eureka | 8761 |
| Kafka | 9092 |
| PostgreSQL medición | 5432 |
| PostgreSQL facturación | 5433 |
| PostgreSQL usuarios | 5435 |

## Variables de entorno

Todas tienen valor por defecto de desarrollo.

| Variable | Servicio | Para qué | Valor por defecto |
| --- | --- | --- | --- |
| `MAIL_USERNAME` | usuarios | Cuenta de Gmail del sistema que envía las alertas. Por defecto, la cuenta desechable del proyecto; opcional | cuenta del proyecto |
| `MAIL_PASSWORD` | usuarios | Contraseña de aplicación de esa cuenta. Por defecto, la de la cuenta del proyecto; opcional | contraseña de la cuenta del proyecto |
| `JWT_SECRET` | usuarios, medicion, facturacion | Secreto de los tokens; debe ser el mismo en los tres | valor de desarrollo |
| `USUARIOS_DB_PASSWORD` | usuarios | Contraseña de la base `usuarios` | valor de desarrollo |
| `MEDICION_DB_PASSWORD` | medicion | Contraseña de la base `medicion` | valor de desarrollo |
| `FACTURACION_DB_PASSWORD` | facturacion | Contraseña de la base `facturacion` | valor de desarrollo |
| `OPERADOR_CONTRASENA` | usuarios | Contraseña del operador inicial | valor de desarrollo |
| `OPERADOR_CORREO` | usuarios | Correo del operador inicial | valor de desarrollo |
| `USUARIOS_GRPC_HOST` y `USUARIOS_GRPC_PORT` | medicion | Dónde está el gRPC de usuarios | localhost / 9091 |

### Correo de alertas

El proyecto trae una cuenta de Gmail desechable creada solo para las alertas de desarrollo, ya configurada en `usuarios-service`: no necesitas hacer nada para recibir correos. **No uses esta cuenta para nada más.**

Si prefieres enviar desde tu propia cuenta, define `MAIL_USERNAME` y `MAIL_PASSWORD` (contraseña de aplicación de 16 letras, con la verificación en dos pasos activa) en la terminal de usuarios-service antes de arrancarlo. Para crear la contraseña: en tu cuenta de Google activa la verificación en dos pasos, abre `myaccount.google.com/apppasswords`, escribe un nombre y copia las 16 letras (solo se muestran una vez).

## Cómo levantar el sistema

Abre **una terminal de PowerShell por servicio**, todas desde la raíz del repositorio. Respeta el orden y espera a ver la línea `Started ... in N seconds` antes de abrir la siguiente.

### 1. Docker Desktop y Kafka

Abre Docker Desktop y espera a que esté en ejecución. Luego:

```powershell
docker compose -f infra/docker-compose.yaml up -d
docker compose -f infra/docker-compose.yaml ps
```

Debe aparecer el contenedor de Kafka en `running`, con el puerto 9092.

### 2. Bases de datos

La base de **medicion-service no se levanta sola** (tiene el arranque automático de Compose deshabilitado). Levántala a mano:

```powershell
docker compose -f medicion-service/docker-compose.yaml up -d
```

Las bases de usuarios y facturación las levanta Spring Boot al arrancar cada servicio, si Docker está abierto. Si prefieres verlas antes de arrancar, también puedes hacerlo a mano:

```powershell
docker compose -f usuarios-service/docker-compose.yaml up -d
docker compose -f facturacion-service/docker-compose.yaml up -d
docker ps
```

Deben verse los contenedores de Postgres en los puertos 5435, 5432 y 5433.

### 3. eureka-server

```powershell
cd eureka-server
.\mvnw.cmd spring-boot:run
```

Abre `http://localhost:8761`: debe cargar el panel de Eureka. Déjalo abierto.

### 4. usuarios-service

En una terminal nueva:

```powershell
cd usuarios-service
.\mvnw.cmd spring-boot:run
```

Las alertas por correo salen de la cuenta de desarrollo del proyecto, sin configuración. Si quieres usar otra cuenta, define antes `$env:MAIL_USERNAME` y `$env:MAIL_PASSWORD`.

En el log debe aparecer la asignación de particiones de los temas `anomalias.detectadas` y `sensores.defectuosos`.

Al arrancar se crea el operador de desarrollo, si no existe ningún usuario con rol operador:

| Rol | Correo | Contraseña (solo desarrollo) |
| --- | --- | --- |
| Operador | `operador@smartgrid.local` | `Operador123!` |

### 5. medicion-service

```powershell
cd medicion-service
.\mvnw.cmd spring-boot:run
```

Arráncalo **después de usuarios-service**: al registrar un medidor, medición consulta a usuarios por gRPC. Si usuarios no responde en 5 segundos, el registro devuelve 503.

### 6. facturacion-service

```powershell
cd facturacion-service
.\mvnw.cmd spring-boot:run
```

### 7. gateway

```powershell
cd gateway
.\mvnw.cmd spring-boot:run
```

Rutas: `/api/medidores/**` y `/api/anomalias/**` van a medición; `/api/facturas/**` y `/api/tarifas/**` a facturación; `/api/auth/**` y `/api/usuarios/**` a usuarios. Vuelve a abrir `http://localhost:8761`: deben estar registrados el gateway y los tres servicios.

### 8. Comprobar el backend

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8000/api/auth/login -ContentType "application/json" -Body '{"correo":"operador@smartgrid.local","contrasena":"Operador123!"}'
```

Debe responder un objeto con `token`, `tipo` y `expiraEnSegundos`. Si responde, funcionan el gateway, usuarios-service y su base de datos.

### 9. Frontend

Sigue el README del repositorio `Smart-Grid-Frontend`. En resumen: `npm install` la primera vez y `npm run dev`, y abrir `http://localhost:5173`.

## Preparación: una tarifa vigente

El sistema no crea tarifas al arrancar. Sin una tarifa vigente no se pueden generar facturas. El operador la crea con `POST /api/tarifas`:

```powershell
$token = (Invoke-RestMethod -Method Post -Uri http://localhost:8000/api/auth/login -ContentType "application/json" -Body '{"correo":"operador@smartgrid.local","contrasena":"Operador123!"}').token
Invoke-RestMethod -Method Post -Uri http://localhost:8000/api/tarifas -Headers @{ Authorization = "Bearer $token" } -ContentType "application/json" -Body '{"valor":850,"vigenciaDesde":"2026-01-01","vigenciaHasta":"2026-12-31"}'
```

Si ya existe una tarifa que cubra esas fechas, el servicio responde con error de superposición; en ese caso no hace falta crearla.

## Probar el sistema completo

Con todo el backend y el frontend en marcha:

| Paso | Quién | Qué hacer | Qué debe pasar |
| --- | --- | --- | --- |
| 1 | Usuario | En `http://localhost:5173/register` crear una cuenta con **un correo real** y contraseña `clave12345` | Cuenta creada; puedes iniciar sesión |
| 2 | Usuario | Entrar a Inicio, Consumo, Alertas y Facturas | Estados vacíos legibles, sin errores (aún no hay medidor) |
| 3 | Operador | Cerrar sesión y entrar con `operador@smartgrid.local` | Aterriza en "Consumo por zona"; el menú solo muestra opciones de operador |
| 4 | Operador | "Registrar medidor": id `30`, ubicación `Tunja Centro`, usuario = la cuenta del paso 1, propiedad `Casa Prueba` | Mensaje de éxito |
| 5 | Operador | Repetir el registro con el id `30` | Error "Ya existe un medidor con id 30" |
| 6 | Operador | "Consumo por zona": zona `Tunja Centro`, fechas `2026-11-01` a `2026-11-30`. Anotar el total (X) | Muestra un total |
| 7 | Medidor | Enviar lecturas con el simulador (siguiente sección) | 4 respuestas 200 |
| 8 | Usuario | Entrar con la cuenta del paso 1: Consumo (período día o mes) | Total 4 kWh con tres lecturas (1, 1, 2); el valor -5 no aparece |
| 9 | Usuario | Alertas | Una anomalía pendiente con valor 2; la lista se actualiza sola cada 30 s |
| 10 | Usuario | Pulsar Resolver | La anomalía pasa a resuelta y sigue así al recargar |
| 11 | Operador | "Consumo por zona" con los mismos filtros del paso 6 | Total X + 4 |
| 12 | Usuario | Revisar el buzón (y Spam) | 2 correos: consumo anómalo (valor 2) y sensor defectuoso (valor -5) |

### Simulador de medidor

El medidor inteligente se simula con un script del repositorio del frontend. Requiere que el medidor ya esté registrado (paso 4). Desde la carpeta `Smart-Grid-Frontend`:

```powershell
node scripts/simulador-medidor.mjs escenario 30
```

Envía cuatro lecturas: 1, 1, 2 (anomalía) y -5 (sensor defectuoso). Las dos primeras responden `esAnomalia: false`, la tercera `esAnomalia: true` y la cuarta `esAnomalia: false` con la razón de descarte. Más opciones en el README del frontend.

### Facturas

La factura se genera sola el día 1 de cada mes a las 02:00 (hora de Bogotá) para el mes anterior de cada medidor. Solo se genera si el período está completo: hay lecturas en ambos extremos del período y la separación entre lecturas consecutivas no supera 24 horas (`medicion.periodo.separacion-maxima`, por defecto `PT24H`). Además debe haber una tarifa vigente.

Para una demostración inmediata, el operador puede generar una factura con `POST /api/facturas` o con las solicitudes de la carpeta `bruno/Facturacion-service`. Una cuenta nueva no tendrá facturas hasta que exista un período completo de lecturas.

## Colección de pruebas de la API (opcional)

La carpeta `bruno/` es una colección de [Bruno](https://www.usebruno.com/): `Usuarios` (18 solicitudes), `Medicion` (11) y `Facturacion-service` (17), con el entorno `environments/Local.bru`. Las cuentas de prueba de la colección usan correos de ejemplo y se crean al ejecutarla; no hacen falta para usar el sistema.

## Pruebas automáticas

Con Docker abierto, en cada módulo:

```powershell
cd usuarios-service
.\mvnw.cmd -q test
cd ..\medicion-service
.\mvnw.cmd -q test
cd ..\facturacion-service
.\mvnw.cmd -q test
cd ..\gateway
.\mvnw.cmd -q test
cd ..\eureka-server
.\mvnw.cmd -q test
```

En la última revisión hay 76 pruebas. Los tests de contexto de usuarios y facturación necesitan Docker en ejecución; sin Docker esos dos fallan y el resto pasa.

## Detener el sistema

1. `Ctrl+C` en cada terminal de servicio.
2. Para detener Kafka y las bases de datos, conservando los contenedores:

```powershell
docker compose -f infra/docker-compose.yaml stop
docker compose -f usuarios-service/docker-compose.yaml stop
docker compose -f medicion-service/docker-compose.yaml stop
docker compose -f facturacion-service/docker-compose.yaml stop
```

Para eliminarlos junto con los datos de prueba, usa `down -v` en lugar de `stop` en cada comando.

## Solución de problemas

| Síntoma | Causa probable | Qué hacer |
| --- | --- | --- |
| `docker version` falla o los tests de contexto dan error | Docker Desktop cerrado | Abre Docker Desktop y espera a que esté en ejecución |
| `Connection refused` a `localhost:9092` | Kafka apagado | `docker compose -f infra/docker-compose.yaml up -d` |
| medicion-service no conecta a su base de datos | Su Postgres no se levanta solo | `docker compose -f medicion-service/docker-compose.yaml up -d` |
| Error 503 al registrar un medidor | usuarios-service apagado o sin registrarse en Eureka | Arráncalo y espera a verlo en `http://localhost:8761` |
| El frontend muestra 401 en todo | Sesión vencida (los tokens duran 60 minutos) o `JWT_SECRET` distinto entre servicios | Cierra sesión y entra de nuevo; si persiste, revisa que los tres servicios usen el mismo `JWT_SECRET` |
| No hay facturas del medidor recién registrado | Facturación copia el medidor por Kafka (tarda unos segundos) o aún no hay un período completo | Espera 10 s tras registrar el medidor; revisa tarifa vigente y lecturas del período |
| No llegan correos o el log muestra `Authentication failed` | La contraseña de la cuenta fue revocada o está mal copiada; o definiste variables `MAIL_*` con datos incorrectos | Si usas variables de entorno, bórralas para volver a la cuenta por defecto; si el problema sigue, avisa al equipo |
| Los correos van a Spam | Cuenta nueva de Gmail en pruebas | Márcalos como "No es spam" |
| Se enviaron lecturas pero no llegó el correo | usuarios-service estaba apagado cuando se publicó el evento | Arráncalo antes de enviar lecturas; los eventos perdidos no se reenvían (`auto-offset-reset: latest`) |
| 403 en una pantalla | El rol no corresponde | Entra con la cuenta del rol correcto |