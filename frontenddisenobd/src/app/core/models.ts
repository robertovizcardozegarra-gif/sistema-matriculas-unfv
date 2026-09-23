export interface LoginResponse {
  token: string;
  tipo: string;
  expiraEn: string;
  usuario: string;
  roles: string[];
}

export interface Facultad {
  codigo: number;
  sigla: string | null;
  nombre: string;
  codigoSede: number | null;
  estado: 'A' | 'I';
}

export interface Escuela {
  codigoFacultad: number;
  codigo: number;
  nombre: string;
  estado: 'A' | 'I';
}

export type EstadoPlan = 'BORRADOR' | 'VIGENTE' | 'NO_VIGENTE' | 'ARCHIVADO';

export interface PlanEstudio {
  codigoFacultad: number;
  codigoEscuela: number;
  correlativo: number;
  anio: number;
  nombre: string;
  resolucion: string | null;
  fechaAprobacion: string | null;
  fechaInicioVigencia: string | null;
  fechaFinVigencia: string | null;
  estado: EstadoPlan;
}

export type TipoCurso = 'OBLIGATORIO' | 'ELECTIVO' | 'ACTIVIDAD' | 'PRACTICA';

export interface Prerequisito {
  codigoCurso: string;
  tipo: 'OBLIGATORIO' | 'RECOMENDADO';
}

export interface Curso {
  codigoFacultad: number;
  codigoEscuela: number;
  correlativoPlan: number;
  codigoCurso: string;
  nombre: string;
  semestre: number;
  horasTeoria: number | null;
  horasPractica: number | null;
  creditos: number;
  tipo: TipoCurso;
  grupoElectivo: string | null;
  estado: 'A' | 'I';
  prerequisitos: Prerequisito[];
}

export interface FacultadRequest {
  codigo?: number | null;
  sigla?: string | null;
  nombre: string;
  codigoSede?: number | null;
  estado: 'A' | 'I';
}

export interface EscuelaRequest {
  codigo?: number | null;
  nombre: string;
  estado: 'A' | 'I';
}

export interface PlanRequest {
  correlativo?: number | null;
  anio: number;
  nombre: string;
  resolucion?: string | null;
  fechaAprobacion?: string | null;
  fechaInicioVigencia?: string | null;
  fechaFinVigencia?: string | null;
  estado: EstadoPlan;
}

export interface CursoRequest {
  codigoCurso: string;
  nombre: string;
  semestre: number;
  horasTeoria: number | null;
  horasPractica: number | null;
  creditos: number;
  tipo: TipoCurso;
  estado: 'A' | 'I';
  prerequisitos: Prerequisito[];
}
