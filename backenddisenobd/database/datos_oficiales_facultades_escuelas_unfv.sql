-- =====================================================================
-- UNFV - Catalogo publico de facultades y carreras de pregrado
-- Consulta realizada: 2026-09-14
--
-- Fuentes principales:
--   https://www.unfv.edu.pe/pregrado
--   https://web.unfv.edu.pe/facultades/
--
-- Criterio del sistema:
-- Cada carrera o especialidad de pregrado se registra como una Escuela,
-- aunque el sitio de una facultad agrupe varias carreras dentro de una
-- misma escuela profesional. Esto permite asociar a cada carrera sus
-- propios planes de estudio y mallas curriculares.
--
-- Requisito: ejecutar antes sistema_matriculas_unfv_v2.sql.
-- Las sedes quedan pendientes de cargar; por eso CodSede se deja NULL.
-- =====================================================================

USE sistema_matriculas_unfv;

START TRANSACTION;

INSERT INTO Facultad (CodFac, Sigla, DenFac, CodSede, Estado) VALUES
  (1,  'FA',    'Facultad de Administración', NULL, 'A'),
  (2,  'FCE',   'Facultad de Ciencias Económicas', NULL, 'A'),
  (3,  'FCFC',  'Facultad de Ciencias Financieras y Contables', NULL, 'A'),
  (4,  'FCCSS', 'Facultad de Ciencias Sociales', NULL, 'A'),
  (5,  'FDCP',  'Facultad de Derecho y Ciencia Política', NULL, 'A'),
  (6,  'FE',    'Facultad de Educación', NULL, 'A'),
  (7,  'FH',    'Facultad de Humanidades', NULL, 'A'),
  (8,  'FAU',   'Facultad de Arquitectura y Urbanismo', NULL, 'A'),
  (9,  'FIC',   'Facultad de Ingeniería Civil', NULL, 'A'),
  (10, 'FIIS',  'Facultad de Ingeniería Industrial y de Sistemas', NULL, 'A'),
  (11, 'FIGAE', 'Facultad de Ingeniería Geográfica, Ambiental y Ecoturismo', NULL, 'A'),
  (12, 'FOPCA', 'Facultad de Oceanografía, Pesquería, Ciencias Alimentarias y Acuicultura', NULL, 'A'),
  (13, 'FIEI',  'Facultad de Ingeniería Electrónica e Informática', NULL, 'A'),
  (14, 'FCCNM', 'Facultad de Ciencias Naturales y Matemática', NULL, 'A'),
  (15, 'FMHU',  'Facultad de Medicina "Hipólito Unanue"', NULL, 'A'),
  (16, 'FO',    'Facultad de Odontología', NULL, 'A'),
  (17, 'FTM',   'Facultad de Tecnología Médica', NULL, 'A'),
  (18, 'FAPS',  'Facultad de Psicología', NULL, 'A');

INSERT INTO Escuela (CodFac, CodEsc, DenEscuela, Estado) VALUES
  -- Facultad de Administración
  (1, 1, 'Administración de Empresas', 'A'),
  (1, 2, 'Administración de Turismo', 'A'),
  (1, 3, 'Administración Pública', 'A'),
  (1, 4, 'Marketing', 'A'),
  (1, 5, 'Negocios Internacionales', 'A'),

  -- Facultad de Ciencias Económicas
  (2, 1, 'Economía', 'A'),

  -- Facultad de Ciencias Financieras y Contables
  (3, 1, 'Contabilidad', 'A'),

  -- Facultad de Ciencias Sociales
  (4, 1, 'Ciencias de la Comunicación', 'A'),
  (4, 2, 'Sociología', 'A'),
  (4, 3, 'Trabajo Social', 'A'),

  -- Facultad de Derecho y Ciencia Política
  (5, 1, 'Derecho', 'A'),
  (5, 2, 'Ciencia Política', 'A'),

  -- Facultad de Educación
  (6, 1,  'Educación Inicial', 'A'),
  (6, 2,  'Educación Primaria', 'A'),
  (6, 3,  'Educación Física', 'A'),
  (6, 4,  'Educación Secundaria - Ciencias Histórico Sociales', 'A'),
  (6, 5,  'Educación Secundaria - Ciencias Naturales', 'A'),
  (6, 6,  'Educación Secundaria - Computación', 'A'),
  (6, 7,  'Educación Secundaria - Filosofía y Ciencias Sociales', 'A'),
  (6, 8,  'Educación Secundaria - Inglés', 'A'),
  (6, 9,  'Educación Secundaria - Lengua y Literatura', 'A'),
  (6, 10, 'Educación Secundaria - Matemática y Física', 'A'),

  -- Facultad de Humanidades
  (7, 1, 'Antropología', 'A'),
  (7, 2, 'Arqueología', 'A'),
  (7, 3, 'Filosofía', 'A'),
  (7, 4, 'Historia', 'A'),
  (7, 5, 'Lingüística', 'A'),
  (7, 6, 'Literatura', 'A'),

  -- Facultad de Arquitectura y Urbanismo
  (8, 1, 'Arquitectura', 'A'),

  -- Facultad de Ingeniería Civil
  (9, 1, 'Ingeniería Civil', 'A'),

  -- Facultad de Ingeniería Industrial y de Sistemas
  (10, 1, 'Ingeniería de Sistemas', 'A'),
  (10, 2, 'Ingeniería de Transportes', 'A'),
  (10, 3, 'Ingeniería Agroindustrial', 'A'),
  (10, 4, 'Ingeniería Industrial', 'A'),

  -- Facultad de Ingeniería Geográfica, Ambiental y Ecoturismo
  (11, 1, 'Ingeniería Geográfica', 'A'),
  (11, 2, 'Ingeniería Ambiental', 'A'),
  (11, 3, 'Ingeniería en Ecoturismo', 'A'),

  -- Facultad de Oceanografía, Pesquería, Ciencias Alimentarias y Acuicultura
  (12, 1, 'Ingeniería Alimentaria', 'A'),
  (12, 2, 'Ingeniería en Acuicultura', 'A'),
  (12, 3, 'Ingeniería Pesquera', 'A'),

  -- Facultad de Ingeniería Electrónica e Informática
  (13, 1, 'Ingeniería Electrónica', 'A'),
  (13, 2, 'Ingeniería Informática', 'A'),
  (13, 3, 'Ingeniería Mecatrónica', 'A'),
  (13, 4, 'Ingeniería de Telecomunicaciones', 'A'),

  -- Facultad de Ciencias Naturales y Matemática
  (14, 1, 'Biología', 'A'),
  (14, 2, 'Estadística', 'A'),
  (14, 3, 'Física', 'A'),
  (14, 4, 'Matemática', 'A'),
  (14, 5, 'Química', 'A'),

  -- Facultad de Medicina "Hipólito Unanue"
  (15, 1, 'Medicina', 'A'),
  (15, 2, 'Enfermería', 'A'),
  (15, 3, 'Nutrición', 'A'),
  (15, 4, 'Obstetricia', 'A'),

  -- Facultad de Odontología
  (16, 1, 'Odontología', 'A'),

  -- Facultad de Tecnología Médica
  (17, 1, 'Terapia Física y Rehabilitación', 'A'),
  (17, 2, 'Terapia de Lenguaje', 'A'),
  (17, 3, 'Laboratorio Clínico y Anatomía Patológica', 'A'),
  (17, 4, 'Radiología', 'A'),
  (17, 5, 'Optometría', 'A'),

  -- Facultad de Psicología
  (18, 1, 'Psicología', 'A');

COMMIT;

