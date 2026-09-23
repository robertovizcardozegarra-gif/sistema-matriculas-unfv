# Backend del sistema de matrículas UNFV

API REST del primer entregable. Permite iniciar sesión como administrador y mantener facultades, carreras (tabla `Escuela`), planes de estudio, cursos y prerrequisitos.

## Requisitos

- Java 17 o superior.
- MySQL 8 o superior.
- La base `sistema_matriculas_unfv` creada con los archivos de la carpeta `database`.

## 1. Preparar MySQL

Ejecuta en este orden:

1. `database/sistema_matriculas_unfv_v2.sql`
2. `database/datos_oficiales_facultades_escuelas_unfv.sql`

El segundo archivo carga las 18 facultades y 60 carreras recopiladas de la web de pregrado de la UNFV.

Para cargar los planes completos 2010 y 2019 de Ingeniería de Sistemas, incluidos prerrequisitos y grupos
electivos, ejecuta después `database/carga_planes_sistemas_2010_2019.sql`.

## 2. Configurar IntelliJ IDEA

Abre esta carpeta como proyecto Maven y espera a que termine de descargar las dependencias. Después abre la configuración de ejecución de `BackenddisenobdApplication` y agrega estas variables de entorno:

```text
DB_USERNAME=root
DB_PASSWORD=TU_CLAVE_MYSQL
APP_ADMIN_USERNAME=admin
APP_ADMIN_PASSWORD=TU_CLAVE_INICIAL
APP_JWT_SECRET=UNA_CLAVE_PRIVADA_DE_AL_MENOS_32_CARACTERES
```

Puedes copiar todos los valores disponibles desde `.env.example`. No subas un archivo `.env` con contraseñas reales.

`APP_ADMIN_PASSWORD` solo se usa para crear el administrador cuando `admin` todavía no existe. La contraseña se guarda con BCrypt; nunca se almacena como texto. En los siguientes arranques no reemplaza al usuario existente.

## 3. Ejecutar

Ejecuta `BackenddisenobdApplication` con el botón verde de IntelliJ. También se puede iniciar desde PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Comprueba el servicio en `GET http://localhost:8080/api/salud`.

## Autenticación

Inicia sesión en `POST /api/auth/login`:

```json
{
  "usuario": "admin",
  "clave": "TU_CLAVE_INICIAL"
}
```

La respuesta incluye un JWT. Angular debe enviarlo en las demás solicitudes:

```text
Authorization: Bearer TOKEN_RECIBIDO
```

Las consultas requieren un usuario autenticado y las operaciones de creación o edición requieren el rol `ADMIN`.

## Endpoints principales

| Método | Ruta | Uso |
|---|---|---|
| POST | `/api/auth/login` | Iniciar sesión |
| GET | `/api/auth/me` | Consultar la sesión actual |
| GET / POST | `/api/facultades` | Listar o crear facultades |
| PUT | `/api/facultades/{facultad}` | Editar o desactivar una facultad |
| GET / POST | `/api/facultades/{facultad}/escuelas` | Listar o agregar carreras |
| PUT | `/api/facultades/{facultad}/escuelas/{escuela}` | Editar o desactivar una carrera |
| GET / POST | `/api/facultades/{facultad}/escuelas/{escuela}/planes` | Listar o crear planes |
| PUT | `/api/facultades/{facultad}/escuelas/{escuela}/planes/{plan}` | Editar el plan y su vigencia |
| GET / POST | `/api/facultades/{facultad}/escuelas/{escuela}/planes/{plan}/cursos` | Listar o agregar cursos |
| POST | `/api/facultades/{facultad}/escuelas/{escuela}/planes/{plan}/cursos/lote` | Agregar hasta 20 cursos en una sola operación |
| PUT | `/api/facultades/{facultad}/escuelas/{escuela}/planes/{plan}/cursos/{curso}` | Editar curso y prerrequisitos |
| DELETE | `/api/facultades/{facultad}/escuelas/{escuela}/planes/{plan}/cursos/{curso}` | Eliminar un curso y sus relaciones de prerrequisito |

Los códigos de facultad, carrera y correlativo del plan pueden omitirse al crear; el backend asignará el siguiente disponible. Los registros se desactivan enviando `"estado": "I"`, sin borrar información histórica.

El archivo `api-ejemplos.http` contiene solicitudes listas para probar desde IntelliJ.

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas utilizan H2 en memoria; no modifican la base MySQL. Cubren autenticación JWT y un flujo completo de facultad, carrera, plan, carga de cursos por lote y prerrequisito.
