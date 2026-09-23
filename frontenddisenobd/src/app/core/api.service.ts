import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  Curso,
  CursoRequest,
  Escuela,
  EscuelaRequest,
  Facultad,
  FacultadRequest,
  PlanEstudio,
  PlanRequest,
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly api = environment.apiUrl;

  listarFacultades(): Observable<Facultad[]> {
    return this.http.get<Facultad[]>(`${this.api}/facultades`);
  }

  crearFacultad(request: FacultadRequest): Observable<Facultad> {
    return this.http.post<Facultad>(`${this.api}/facultades`, request);
  }

  listarEscuelas(codigoFacultad: number): Observable<Escuela[]> {
    return this.http.get<Escuela[]>(`${this.api}/facultades/${codigoFacultad}/escuelas`);
  }

  crearEscuela(codigoFacultad: number, request: EscuelaRequest): Observable<Escuela> {
    return this.http.post<Escuela>(`${this.api}/facultades/${codigoFacultad}/escuelas`, request);
  }

  listarPlanes(codigoFacultad: number, codigoEscuela: number): Observable<PlanEstudio[]> {
    return this.http.get<PlanEstudio[]>(
      `${this.api}/facultades/${codigoFacultad}/escuelas/${codigoEscuela}/planes`,
    );
  }

  crearPlan(codigoFacultad: number, codigoEscuela: number, request: PlanRequest): Observable<PlanEstudio> {
    return this.http.post<PlanEstudio>(
      `${this.api}/facultades/${codigoFacultad}/escuelas/${codigoEscuela}/planes`,
      request,
    );
  }

  actualizarPlan(
    codigoFacultad: number,
    codigoEscuela: number,
    correlativo: number,
    request: PlanRequest,
  ): Observable<PlanEstudio> {
    return this.http.put<PlanEstudio>(
      `${this.api}/facultades/${codigoFacultad}/escuelas/${codigoEscuela}/planes/${correlativo}`,
      request,
    );
  }

  listarCursos(codigoFacultad: number, codigoEscuela: number, plan: number): Observable<Curso[]> {
    return this.http.get<Curso[]>(
      `${this.api}/facultades/${codigoFacultad}/escuelas/${codigoEscuela}/planes/${plan}/cursos`,
    );
  }

  crearCurso(
    codigoFacultad: number,
    codigoEscuela: number,
    plan: number,
    request: CursoRequest,
  ): Observable<Curso> {
    return this.http.post<Curso>(
      `${this.api}/facultades/${codigoFacultad}/escuelas/${codigoEscuela}/planes/${plan}/cursos`,
      request,
    );
  }

  crearCursos(
    codigoFacultad: number,
    codigoEscuela: number,
    plan: number,
    requests: CursoRequest[],
  ): Observable<Curso[]> {
    return this.http.post<Curso[]>(
      `${this.api}/facultades/${codigoFacultad}/escuelas/${codigoEscuela}/planes/${plan}/cursos/lote`,
      requests,
    );
  }

  actualizarCurso(
    codigoFacultad: number,
    codigoEscuela: number,
    plan: number,
    codigoCurso: string,
    request: CursoRequest,
  ): Observable<Curso> {
    return this.http.put<Curso>(
      `${this.api}/facultades/${codigoFacultad}/escuelas/${codigoEscuela}/planes/${plan}/cursos/${encodeURIComponent(codigoCurso)}`,
      request,
    );
  }

  eliminarCurso(
    codigoFacultad: number,
    codigoEscuela: number,
    plan: number,
    codigoCurso: string,
  ): Observable<void> {
    return this.http.delete<void>(
      `${this.api}/facultades/${codigoFacultad}/escuelas/${codigoEscuela}/planes/${plan}/cursos/${encodeURIComponent(codigoCurso)}`,
    );
  }
}
