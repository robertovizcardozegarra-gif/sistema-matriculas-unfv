# Frontend del sistema de matrículas UNFV

Interfaz Angular para administrar las mallas curriculares del primer entregable. Se conecta con el backend Spring Boot del proyecto.

## Funciones incluidas

- Inicio de sesión del administrador mediante JWT.
- Selección de facultad y carrera profesional.
- Registro de nuevas facultades y carreras.
- Creación y edición de planes curriculares.
- Registro simultáneo de varios cursos: se eligen el ciclo y la cantidad, y la tabla genera las filas exactas.
- Edición individual de los cursos registrados.
- Horas teóricas, horas prácticas, créditos y tipo obligatorio o electivo.
- Buscador de prerrequisitos por código o nombre, con selección múltiple.
- Para los electivos se pueden seleccionar como prerrequisitos cursos obligatorios o electivos anteriores.
- Los grupos electivos muestran todas sus alternativas, pero los totales cuentan solo una elección por grupo.
- Créditos enteros y totales de horas teóricas, horas prácticas y créditos por ciclo.
- Eliminación de cursos con confirmación y limpieza de sus relaciones de prerrequisito.
- Resumen de cursos, créditos y ciclos registrados.
- Diseño adaptable para computadora, tablet y celular.

## Antes de comenzar

El backend debe estar ejecutándose en:

```text
http://localhost:8080
```

Si utilizas otra dirección, cambia `apiUrl` en `src/environments/environment.ts`.

## Abrir en Visual Studio Code

1. Descomprime este proyecto.
2. Abre la carpeta `frontend-matriculas-unfv` en Visual Studio Code.
3. Abre una terminal y ejecuta:

```powershell
npm install
npm start
```

4. Ingresa en `http://localhost:4200`.
5. Inicia sesión con el administrador configurado en el backend.

El backend acepta solicitudes desde `http://localhost:4200` de manera predeterminada.

## Flujo para registrar una malla

1. Selecciona la facultad.
2. Selecciona la carrera profesional.
3. Crea o elige un plan curricular.
4. Presiona **Agregar cursos**, selecciona el ciclo y escribe cuántos cursos tendrá.
5. Completa las filas generadas y guarda el lote completo.
6. Al crear los cursos posteriores, selecciona sus prerrequisitos entre los cursos que ya existen en el plan.

Cada lote admite hasta 20 cursos. El backend guarda todos los cursos juntos; si una fila contiene un error, no se registra parcialmente el resto del lote.

Los códigos internos de nuevas facultades, carreras y planes son asignados por el backend. Los cursos sí requieren un código académico.

## Comandos disponibles

```powershell
npm start
npm run build
npm test -- --watch=false
```
