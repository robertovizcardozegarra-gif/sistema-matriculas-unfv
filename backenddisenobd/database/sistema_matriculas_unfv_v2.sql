-- =====================================================================
-- Sistema de Matriculas - Universidad Nacional Federico Villarreal
-- Esquema MySQL v2
--
-- Alcance:
--   * Conserva todas las tablas del esquema original.
--   * No contiene datos de prueba.
--   * Permite codigos de curso alfanumericos.
--   * Agrega autenticacion, roles y documentos de planes de estudio.
--
-- ADVERTENCIA: al ejecutar este archivo se eliminan y recrean las tablas
-- de la base de datos sistema_matriculas_unfv.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS sistema_matriculas_unfv
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sistema_matriculas_unfv;

SET FOREIGN_KEY_CHECKS = 0;

-- Las tablas se eliminan en orden inverso a sus dependencias.
DROP TABLE IF EXISTS DocumentoPlanEstudio;
DROP TABLE IF EXISTS UsuarioDocente;
DROP TABLE IF EXISTS UsuarioEstudiante;
DROP TABLE IF EXISTS UsuarioRol;
DROP TABLE IF EXISTS HistorialAcademico;
DROP TABLE IF EXISTS DetalleMatricula;
DROP TABLE IF EXISTS Matricula;
DROP TABLE IF EXISTS Horario;
DROP TABLE IF EXISTS Seccion;
DROP TABLE IF EXISTS Aula;
DROP TABLE IF EXISTS Estudiante;
DROP TABLE IF EXISTS PeriodoAcademico;
DROP TABLE IF EXISTS Docente;
DROP TABLE IF EXISTS MezclaCurso;
DROP TABLE IF EXISTS Curso;
DROP TABLE IF EXISTS PlanEstudio;
DROP TABLE IF EXISTS Escuela;
DROP TABLE IF EXISTS Facultad;
DROP TABLE IF EXISTS Sede;
DROP TABLE IF EXISTS Usuario;
DROP TABLE IF EXISTS Rol;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- Estructura institucional y academica
-- =====================================================================

CREATE TABLE Sede (
  CodSede    INT NOT NULL AUTO_INCREMENT,
  DenSede    VARCHAR(100) NOT NULL,
  Direccion  VARCHAR(180),
  Estado     CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (CodSede),
  UNIQUE KEY uq_sede_denominacion (DenSede),
  CONSTRAINT chk_sede_estado CHECK (Estado IN ('A', 'I'))
) ENGINE=InnoDB;

CREATE TABLE Facultad (
  CodFac   INT NOT NULL,
  Sigla    VARCHAR(10),
  DenFac   VARCHAR(150) NOT NULL,
  CodSede  INT,
  Estado   CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (CodFac),
  UNIQUE KEY uq_facultad_sigla (Sigla),
  UNIQUE KEY uq_facultad_denominacion (DenFac),
  KEY idx_facultad_sede (CodSede),
  CONSTRAINT fk_facultad_sede FOREIGN KEY (CodSede)
    REFERENCES Sede (CodSede),
  CONSTRAINT chk_facultad_estado CHECK (Estado IN ('A', 'I'))
) ENGINE=InnoDB;

CREATE TABLE Escuela (
  CodFac      INT NOT NULL,
  CodEsc      INT NOT NULL,
  DenEscuela  VARCHAR(150) NOT NULL,
  Estado      CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (CodFac, CodEsc),
  UNIQUE KEY uq_escuela_denominacion (CodFac, DenEscuela),
  CONSTRAINT fk_escuela_facultad FOREIGN KEY (CodFac)
    REFERENCES Facultad (CodFac),
  CONSTRAINT chk_escuela_estado CHECK (Estado IN ('A', 'I'))
) ENGINE=InnoDB;

CREATE TABLE PlanEstudio (
  CodFac               INT NOT NULL,
  CodEsc               INT NOT NULL,
  CorrPE               INT NOT NULL,
  AnioPE               SMALLINT NOT NULL,
  DenPlan               VARCHAR(150) NOT NULL,
  Resolucion            VARCHAR(100),
  FechaAprobacion       DATE,
  FechaInicioVigencia   DATE,
  FechaFinVigencia      DATE,
  Estado                VARCHAR(15) NOT NULL DEFAULT 'BORRADOR',
  PRIMARY KEY (CodFac, CodEsc, CorrPE),
  UNIQUE KEY uq_plan_escuela_anio (CodFac, CodEsc, AnioPE),
  CONSTRAINT fk_planestudio_escuela FOREIGN KEY (CodFac, CodEsc)
    REFERENCES Escuela (CodFac, CodEsc),
  CONSTRAINT chk_plan_anio CHECK (AnioPE BETWEEN 1900 AND 2200),
  CONSTRAINT chk_plan_estado CHECK (
    Estado IN ('BORRADOR', 'VIGENTE', 'NO_VIGENTE', 'ARCHIVADO')
  ),
  CONSTRAINT chk_plan_vigencia CHECK (
    FechaFinVigencia IS NULL
    OR FechaInicioVigencia IS NULL
    OR FechaFinVigencia >= FechaInicioVigencia
  )
) ENGINE=InnoDB;

CREATE TABLE GrupoElectivo (
  CodFac               INT NOT NULL,
  CodEsc               INT NOT NULL,
  CorrPE               INT NOT NULL,
  CodGrupo             VARCHAR(20) NOT NULL,
  DenGrupo             VARCHAR(150) NOT NULL,
  Semestre             TINYINT NOT NULL,
  CantidadRequerida    TINYINT NOT NULL DEFAULT 1,
  CreditosComputables  DECIMAL(4,1) NOT NULL,
  Estado               CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (CodFac, CodEsc, CorrPE, CodGrupo),
  CONSTRAINT fk_grupo_electivo_plan FOREIGN KEY (CodFac, CodEsc, CorrPE)
    REFERENCES PlanEstudio (CodFac, CodEsc, CorrPE),
  CONSTRAINT chk_grupo_electivo_semestre CHECK (Semestre BETWEEN 1 AND 10),
  CONSTRAINT chk_grupo_electivo_cantidad CHECK (CantidadRequerida > 0),
  CONSTRAINT chk_grupo_electivo_creditos CHECK (CreditosComputables > 0),
  CONSTRAINT chk_grupo_electivo_estado CHECK (Estado IN ('A', 'I'))
) ENGINE=InnoDB;

CREATE TABLE Curso (
  CodFac      INT NOT NULL,
  CodEsc      INT NOT NULL,
  CorrPE      INT NOT NULL,
  CodCurso    VARCHAR(20) NOT NULL,
  DenCurso    VARCHAR(180) NOT NULL,
  Semestre    TINYINT NOT NULL,
  HT          TINYINT,
  HP          TINYINT,
  Cred        DECIMAL(4,1) NOT NULL,
  TipoCurso   VARCHAR(15) NOT NULL DEFAULT 'OBLIGATORIO',
  CodGrupoElectivo VARCHAR(20),
  PreReq      CHAR(1) NOT NULL DEFAULT 'N',
  Estado      CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (CodFac, CodEsc, CorrPE, CodCurso),
  KEY idx_curso_plan_semestre (CodFac, CodEsc, CorrPE, Semestre),
  KEY idx_curso_grupo_electivo (CodFac, CodEsc, CorrPE, CodGrupoElectivo),
  CONSTRAINT fk_curso_planestudio FOREIGN KEY (CodFac, CodEsc, CorrPE)
    REFERENCES PlanEstudio (CodFac, CodEsc, CorrPE),
  CONSTRAINT fk_curso_grupo_electivo FOREIGN KEY (CodFac, CodEsc, CorrPE, CodGrupoElectivo)
    REFERENCES GrupoElectivo (CodFac, CodEsc, CorrPE, CodGrupo),
  CONSTRAINT chk_curso_semestre CHECK (Semestre BETWEEN 1 AND 10),
  CONSTRAINT chk_curso_ht CHECK (HT IS NULL OR HT >= 0),
  CONSTRAINT chk_curso_hp CHECK (HP IS NULL OR HP >= 0),
  CONSTRAINT chk_curso_creditos CHECK (Cred > 0 AND Cred = FLOOR(Cred)),
  CONSTRAINT chk_curso_tipo CHECK (
    TipoCurso IN ('OBLIGATORIO', 'ELECTIVO', 'ACTIVIDAD', 'PRACTICA')
  ),
  CONSTRAINT chk_curso_prereq CHECK (PreReq IN ('S', 'N')),
  CONSTRAINT chk_curso_estado CHECK (Estado IN ('A', 'I'))
) ENGINE=InnoDB;

CREATE TABLE MezclaCurso (
  CodFac                INT NOT NULL,
  CodEsc                INT NOT NULL,
  CorrPE                INT NOT NULL,
  CodCurso              VARCHAR(20) NOT NULL,
  CodCursoPreRequisito  VARCHAR(20) NOT NULL,
  TipoRequisito         VARCHAR(15) NOT NULL DEFAULT 'OBLIGATORIO',
  PRIMARY KEY (
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso,
    CodCursoPreRequisito
  ),
  KEY idx_mezcla_prerequisito (
    CodFac,
    CodEsc,
    CorrPE,
    CodCursoPreRequisito
  ),
  CONSTRAINT fk_mezcla_curso FOREIGN KEY (
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso
  ) REFERENCES Curso (CodFac, CodEsc, CorrPE, CodCurso),
  CONSTRAINT fk_mezcla_prerequisito FOREIGN KEY (
    CodFac,
    CodEsc,
    CorrPE,
    CodCursoPreRequisito
  ) REFERENCES Curso (CodFac, CodEsc, CorrPE, CodCurso),
  CONSTRAINT chk_mezcla_cursos_distintos CHECK (
    CodCurso <> CodCursoPreRequisito
  ),
  CONSTRAINT chk_mezcla_tipo CHECK (
    TipoRequisito IN ('OBLIGATORIO', 'RECOMENDADO')
  )
) ENGINE=InnoDB;

-- =====================================================================
-- Seguridad y autenticacion
-- =====================================================================

CREATE TABLE Rol (
  CodRol       INT NOT NULL AUTO_INCREMENT,
  NombreRol    VARCHAR(30) NOT NULL,
  Descripcion  VARCHAR(150),
  Estado       CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (CodRol),
  UNIQUE KEY uq_rol_nombre (NombreRol),
  CONSTRAINT chk_rol_estado CHECK (Estado IN ('A', 'I'))
) ENGINE=InnoDB;

CREATE TABLE Usuario (
  CodUsuario       BIGINT NOT NULL AUTO_INCREMENT,
  NombreUsuario    VARCHAR(60) NOT NULL,
  ClaveHash        VARCHAR(255) NOT NULL,
  Correo           VARCHAR(150),
  Nombres          VARCHAR(100) NOT NULL,
  Apellidos        VARCHAR(100) NOT NULL,
  Estado           CHAR(1) NOT NULL DEFAULT 'A',
  IntentosFallidos TINYINT NOT NULL DEFAULT 0,
  BloqueadoHasta   DATETIME,
  FechaCreacion    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UltimoAcceso     DATETIME,
  PRIMARY KEY (CodUsuario),
  UNIQUE KEY uq_usuario_nombre (NombreUsuario),
  UNIQUE KEY uq_usuario_correo (Correo),
  CONSTRAINT chk_usuario_estado CHECK (Estado IN ('A', 'I')),
  CONSTRAINT chk_usuario_intentos CHECK (IntentosFallidos >= 0)
) ENGINE=InnoDB;

CREATE TABLE UsuarioRol (
  CodUsuario  BIGINT NOT NULL,
  CodRol      INT NOT NULL,
  PRIMARY KEY (CodUsuario, CodRol),
  KEY idx_usuario_rol_rol (CodRol),
  CONSTRAINT fk_usuario_rol_usuario FOREIGN KEY (CodUsuario)
    REFERENCES Usuario (CodUsuario),
  CONSTRAINT fk_usuario_rol_rol FOREIGN KEY (CodRol)
    REFERENCES Rol (CodRol)
) ENGINE=InnoDB;

-- =====================================================================
-- Personas, periodos y oferta academica
-- =====================================================================

CREATE TABLE Docente (
  CodDocente      INT NOT NULL AUTO_INCREMENT,
  Nombres         VARCHAR(100) NOT NULL,
  Apellidos       VARCHAR(100) NOT NULL,
  DNI             CHAR(8) NOT NULL,
  Correo          VARCHAR(150),
  GradoAcademico  VARCHAR(80),
  CodFac          INT NOT NULL,
  Estado          CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (CodDocente),
  UNIQUE KEY uq_docente_dni (DNI),
  UNIQUE KEY uq_docente_correo (Correo),
  KEY idx_docente_facultad (CodFac),
  CONSTRAINT fk_docente_facultad FOREIGN KEY (CodFac)
    REFERENCES Facultad (CodFac),
  CONSTRAINT chk_docente_estado CHECK (Estado IN ('A', 'I'))
) ENGINE=InnoDB;

CREATE TABLE PeriodoAcademico (
  CodPeriodo   INT NOT NULL AUTO_INCREMENT,
  Anio         SMALLINT NOT NULL,
  Ciclo        CHAR(2) NOT NULL,
  FechaInicio  DATE NOT NULL,
  FechaFin     DATE NOT NULL,
  Estado       VARCHAR(15) NOT NULL DEFAULT 'PLANIFICADO',
  PRIMARY KEY (CodPeriodo),
  UNIQUE KEY uq_periodo_anio_ciclo (Anio, Ciclo),
  CONSTRAINT chk_periodo_anio CHECK (Anio BETWEEN 1900 AND 2200),
  CONSTRAINT chk_periodo_ciclo CHECK (Ciclo IN ('I', 'II')),
  CONSTRAINT chk_periodo_estado CHECK (
    Estado IN ('PLANIFICADO', 'ACTIVO', 'CERRADO')
  ),
  CONSTRAINT chk_periodo_fechas CHECK (FechaFin > FechaInicio)
) ENGINE=InnoDB;

CREATE TABLE Estudiante (
  CodEstudiante    INT NOT NULL,
  Nombres          VARCHAR(100) NOT NULL,
  Apellidos        VARCHAR(100) NOT NULL,
  DNI              CHAR(8) NOT NULL,
  FechaNacimiento  DATE NOT NULL,
  Correo            VARCHAR(150) NOT NULL,
  CodFac            INT NOT NULL,
  CodEsc            INT NOT NULL,
  CorrPE            INT NOT NULL,
  Estado            CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (CodEstudiante),
  UNIQUE KEY uq_estudiante_dni (DNI),
  UNIQUE KEY uq_estudiante_correo (Correo),
  KEY idx_estudiante_plan (CodFac, CodEsc, CorrPE),
  CONSTRAINT fk_estudiante_planestudio FOREIGN KEY (CodFac, CodEsc, CorrPE)
    REFERENCES PlanEstudio (CodFac, CodEsc, CorrPE),
  CONSTRAINT chk_estudiante_estado CHECK (Estado IN ('A', 'I'))
) ENGINE=InnoDB;

CREATE TABLE UsuarioEstudiante (
  CodUsuario     BIGINT NOT NULL,
  CodEstudiante  INT NOT NULL,
  PRIMARY KEY (CodUsuario),
  UNIQUE KEY uq_usuario_estudiante (CodEstudiante),
  CONSTRAINT fk_usuario_estudiante_usuario FOREIGN KEY (CodUsuario)
    REFERENCES Usuario (CodUsuario),
  CONSTRAINT fk_usuario_estudiante_estudiante FOREIGN KEY (CodEstudiante)
    REFERENCES Estudiante (CodEstudiante)
) ENGINE=InnoDB;

CREATE TABLE UsuarioDocente (
  CodUsuario  BIGINT NOT NULL,
  CodDocente  INT NOT NULL,
  PRIMARY KEY (CodUsuario),
  UNIQUE KEY uq_usuario_docente (CodDocente),
  CONSTRAINT fk_usuario_docente_usuario FOREIGN KEY (CodUsuario)
    REFERENCES Usuario (CodUsuario),
  CONSTRAINT fk_usuario_docente_docente FOREIGN KEY (CodDocente)
    REFERENCES Docente (CodDocente)
) ENGINE=InnoDB;

CREATE TABLE Aula (
  CodAula    INT NOT NULL AUTO_INCREMENT,
  CodSede    INT NOT NULL,
  Nombre     VARCHAR(30) NOT NULL,
  Pabellon   VARCHAR(80) NOT NULL,
  Capacidad  SMALLINT NOT NULL,
  Estado     CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (CodAula),
  UNIQUE KEY uq_aula_sede_pabellon_nombre (CodSede, Pabellon, Nombre),
  CONSTRAINT fk_aula_sede FOREIGN KEY (CodSede)
    REFERENCES Sede (CodSede),
  CONSTRAINT chk_aula_capacidad CHECK (Capacidad > 0),
  CONSTRAINT chk_aula_estado CHECK (Estado IN ('A', 'I'))
) ENGINE=InnoDB;

CREATE TABLE Seccion (
  CodFac       INT NOT NULL,
  CodEsc       INT NOT NULL,
  CorrPE       INT NOT NULL,
  CodCurso     VARCHAR(20) NOT NULL,
  CodPeriodo   INT NOT NULL,
  CodSeccion   VARCHAR(10) NOT NULL,
  CodDocente   INT NOT NULL,
  Vacantes     SMALLINT NOT NULL,
  Estado       VARCHAR(15) NOT NULL DEFAULT 'PLANIFICADA',
  PRIMARY KEY (
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso,
    CodPeriodo,
    CodSeccion
  ),
  KEY idx_seccion_periodo (CodPeriodo),
  KEY idx_seccion_docente (CodDocente),
  CONSTRAINT fk_seccion_curso FOREIGN KEY (
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso
  ) REFERENCES Curso (CodFac, CodEsc, CorrPE, CodCurso),
  CONSTRAINT fk_seccion_periodo FOREIGN KEY (CodPeriodo)
    REFERENCES PeriodoAcademico (CodPeriodo),
  CONSTRAINT fk_seccion_docente FOREIGN KEY (CodDocente)
    REFERENCES Docente (CodDocente),
  CONSTRAINT chk_seccion_vacantes CHECK (Vacantes >= 0),
  CONSTRAINT chk_seccion_estado CHECK (
    Estado IN ('PLANIFICADA', 'ABIERTA', 'CERRADA', 'CANCELADA')
  )
) ENGINE=InnoDB;

CREATE TABLE Horario (
  CodFac      INT NOT NULL,
  CodEsc      INT NOT NULL,
  CorrPE      INT NOT NULL,
  CodCurso    VARCHAR(20) NOT NULL,
  CodPeriodo  INT NOT NULL,
  CodSeccion  VARCHAR(10) NOT NULL,
  CodHorario  INT NOT NULL,
  DiaSemana   VARCHAR(10) NOT NULL,
  HoraInicio  TIME NOT NULL,
  HoraFin     TIME NOT NULL,
  CodAula     INT NOT NULL,
  PRIMARY KEY (
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso,
    CodPeriodo,
    CodSeccion,
    CodHorario
  ),
  KEY idx_horario_aula (CodAula),
  CONSTRAINT fk_horario_seccion FOREIGN KEY (
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso,
    CodPeriodo,
    CodSeccion
  ) REFERENCES Seccion (
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso,
    CodPeriodo,
    CodSeccion
  ),
  CONSTRAINT fk_horario_aula FOREIGN KEY (CodAula)
    REFERENCES Aula (CodAula),
  CONSTRAINT chk_horario_dia CHECK (
    DiaSemana IN (
      'LUNES',
      'MARTES',
      'MIERCOLES',
      'JUEVES',
      'VIERNES',
      'SABADO',
      'DOMINGO'
    )
  ),
  CONSTRAINT chk_horario_horas CHECK (HoraFin > HoraInicio)
) ENGINE=InnoDB;

-- =====================================================================
-- Matricula e historial academico
-- =====================================================================

CREATE TABLE Matricula (
  CodMatricula    BIGINT NOT NULL AUTO_INCREMENT,
  CodEstudiante   INT NOT NULL,
  CodPeriodo      INT NOT NULL,
  FechaMatricula  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Estado          VARCHAR(20) NOT NULL DEFAULT 'REGISTRADA',
  PRIMARY KEY (CodMatricula),
  UNIQUE KEY uq_matricula_estudiante_periodo (CodEstudiante, CodPeriodo),
  KEY idx_matricula_periodo (CodPeriodo),
  CONSTRAINT fk_matricula_estudiante FOREIGN KEY (CodEstudiante)
    REFERENCES Estudiante (CodEstudiante),
  CONSTRAINT fk_matricula_periodo FOREIGN KEY (CodPeriodo)
    REFERENCES PeriodoAcademico (CodPeriodo),
  CONSTRAINT chk_matricula_estado CHECK (
    Estado IN ('REGISTRADA', 'CONFIRMADA', 'ANULADA')
  )
) ENGINE=InnoDB;

CREATE TABLE DetalleMatricula (
  CodMatricula  BIGINT NOT NULL,
  CodFac        INT NOT NULL,
  CodEsc        INT NOT NULL,
  CorrPE        INT NOT NULL,
  CodCurso      VARCHAR(20) NOT NULL,
  CodPeriodo    INT NOT NULL,
  CodSeccion    VARCHAR(10) NOT NULL,
  EstadoCurso   VARCHAR(20) NOT NULL DEFAULT 'MATRICULADO',
  PRIMARY KEY (
    CodMatricula,
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso,
    CodPeriodo,
    CodSeccion
  ),
  KEY idx_detalle_seccion (
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso,
    CodPeriodo,
    CodSeccion
  ),
  CONSTRAINT fk_detalle_matricula FOREIGN KEY (CodMatricula)
    REFERENCES Matricula (CodMatricula),
  CONSTRAINT fk_detalle_seccion FOREIGN KEY (
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso,
    CodPeriodo,
    CodSeccion
  ) REFERENCES Seccion (
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso,
    CodPeriodo,
    CodSeccion
  ),
  CONSTRAINT chk_detalle_estado CHECK (
    EstadoCurso IN ('MATRICULADO', 'RETIRADO', 'ANULADO')
  )
) ENGINE=InnoDB;

CREATE TABLE HistorialAcademico (
  CodEstudiante  INT NOT NULL,
  CodFac         INT NOT NULL,
  CodEsc         INT NOT NULL,
  CorrPE         INT NOT NULL,
  CodCurso       VARCHAR(20) NOT NULL,
  CodPeriodo     INT NOT NULL,
  Intento        TINYINT NOT NULL DEFAULT 1,
  NotaFinal      DECIMAL(4,2),
  EstadoCurso    VARCHAR(20) NOT NULL,
  PRIMARY KEY (
    CodEstudiante,
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso,
    CodPeriodo,
    Intento
  ),
  KEY idx_historial_curso (CodFac, CodEsc, CorrPE, CodCurso),
  KEY idx_historial_periodo (CodPeriodo),
  CONSTRAINT fk_historial_estudiante FOREIGN KEY (CodEstudiante)
    REFERENCES Estudiante (CodEstudiante),
  CONSTRAINT fk_historial_curso FOREIGN KEY (
    CodFac,
    CodEsc,
    CorrPE,
    CodCurso
  ) REFERENCES Curso (CodFac, CodEsc, CorrPE, CodCurso),
  CONSTRAINT fk_historial_periodo FOREIGN KEY (CodPeriodo)
    REFERENCES PeriodoAcademico (CodPeriodo),
  CONSTRAINT chk_historial_intento CHECK (Intento > 0),
  CONSTRAINT chk_historial_nota CHECK (
    NotaFinal IS NULL OR NotaFinal BETWEEN 0 AND 20
  ),
  CONSTRAINT chk_historial_estado CHECK (
    EstadoCurso IN (
      'EN_CURSO',
      'APROBADO',
      'DESAPROBADO',
      'RETIRADO',
      'CONVALIDADO'
    )
  )
) ENGINE=InnoDB;

-- =====================================================================
-- Documentos de respaldo de los planes de estudio
-- =====================================================================

CREATE TABLE DocumentoPlanEstudio (
  CodDocumento      BIGINT NOT NULL AUTO_INCREMENT,
  CodFac            INT NOT NULL,
  CodEsc            INT NOT NULL,
  CorrPE            INT NOT NULL,
  NombreOriginal    VARCHAR(255) NOT NULL,
  NombreAlmacenado  VARCHAR(255) NOT NULL,
  TipoMime          VARCHAR(100) NOT NULL,
  TamanioBytes      BIGINT NOT NULL,
  RutaArchivo       VARCHAR(500) NOT NULL,
  HashSha256        CHAR(64),
  CodUsuarioCarga   BIGINT NOT NULL,
  FechaCarga        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  Estado            CHAR(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (CodDocumento),
  KEY idx_documento_plan (CodFac, CodEsc, CorrPE),
  KEY idx_documento_usuario (CodUsuarioCarga),
  CONSTRAINT fk_documento_plan FOREIGN KEY (CodFac, CodEsc, CorrPE)
    REFERENCES PlanEstudio (CodFac, CodEsc, CorrPE),
  CONSTRAINT fk_documento_usuario FOREIGN KEY (CodUsuarioCarga)
    REFERENCES Usuario (CodUsuario),
  CONSTRAINT chk_documento_tamanio CHECK (TamanioBytes > 0),
  CONSTRAINT chk_documento_estado CHECK (Estado IN ('A', 'I'))
) ENGINE=InnoDB;

-- =====================================================================
-- Datos iniciales
-- =====================================================================
-- Este archivo no inserta facultades, escuelas, planes, cursos, roles ni
-- usuarios. Los catalogos oficiales y el administrador inicial se deben
-- cargar posteriormente mediante un script de datos separado.
