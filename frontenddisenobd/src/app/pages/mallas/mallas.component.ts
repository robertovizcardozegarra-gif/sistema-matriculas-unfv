import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import {
  Curso,
  CursoRequest,
  Escuela,
  EstadoPlan,
  Facultad,
  PlanEstudio,
  PlanRequest,
  Prerequisito,
  TipoCurso,
} from '../../core/models';

type Modal = 'facultad' | 'escuela' | 'plan' | 'cursos' | 'curso' | null;

@Component({
  selector: 'app-mallas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './mallas.component.html',
  styleUrl: './mallas.component.css',
})
export class MallasComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly usuario = this.auth.usuario;
  readonly facultades = signal<Facultad[]>([]);
  readonly escuelas = signal<Escuela[]>([]);
  readonly planes = signal<PlanEstudio[]>([]);
  readonly cursos = signal<Curso[]>([]);

  readonly codigoFacultad = signal<number | null>(null);
  readonly codigoEscuela = signal<number | null>(null);
  readonly correlativoPlan = signal<number | null>(null);
  readonly modal = signal<Modal>(null);
  readonly cargando = signal(false);
  readonly guardando = signal(false);
  readonly error = signal('');
  readonly aviso = signal('');
  readonly cursoEditando = signal<Curso | null>(null);
  readonly planEditando = signal<PlanEstudio | null>(null);
  readonly prerequisitosSeleccionados = signal<Record<string, Prerequisito['tipo']>>({});
  readonly busquedaPrerequisitoEdicion = signal('');
  readonly numerosCiclo = Array.from({ length: 10 }, (_, index) => index + 1);

  readonly facultadActual = computed(() =>
    this.facultades().find((item) => item.codigo === this.codigoFacultad()),
  );
  readonly escuelaActual = computed(() =>
    this.escuelas().find((item) => item.codigo === this.codigoEscuela()),
  );
  readonly planActual = computed(() =>
    this.planes().find((item) => item.correlativo === this.correlativoPlan()),
  );
  readonly totalCreditos = computed(() =>
    this.cursosComputables(this.cursos())
      .filter((curso) => curso.estado === 'A')
      .reduce((total, curso) => total + Number(curso.creditos), 0),
  );
  readonly totalCursosCurriculares = computed(() => this.cursosComputables(this.cursos()).length);
  readonly ciclos = computed(() =>
    Array.from({ length: 10 }, (_, index) => ({
      numero: index + 1,
      cursos: this.cursos().filter((curso) => curso.semestre === index + 1),
    })).filter((ciclo) => ciclo.cursos.length > 0),
  );

  readonly facultadForm = this.fb.nonNullable.group({
    sigla: ['', [Validators.maxLength(10)]],
    nombre: ['', [Validators.required, Validators.maxLength(150)]],
  });

  readonly escuelaForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(150)]],
  });

  readonly planForm = this.fb.nonNullable.group({
    anio: [new Date().getFullYear(), [Validators.required, Validators.min(1900), Validators.max(2200)]],
    nombre: ['', [Validators.required, Validators.maxLength(150)]],
    resolucion: ['', [Validators.maxLength(100)]],
    fechaAprobacion: [''],
    fechaInicioVigencia: [''],
    fechaFinVigencia: [''],
    estado: ['BORRADOR' as EstadoPlan, [Validators.required]],
  });

  readonly cursoForm = this.fb.nonNullable.group({
    codigoCurso: ['', [Validators.required, Validators.maxLength(20)]],
    nombre: ['', [Validators.required, Validators.maxLength(180)]],
    semestre: [1, [Validators.required, Validators.min(1), Validators.max(10)]],
    horasTeoria: [0, [Validators.required, Validators.min(0)]],
    horasPractica: [0, [Validators.required, Validators.min(0)]],
    creditos: [1, [Validators.required, Validators.min(1), Validators.max(99), Validators.pattern(/^\d+$/)]],
    tipo: ['OBLIGATORIO' as TipoCurso, [Validators.required]],
  });

  readonly cursosLoteForm = this.fb.nonNullable.group({
    ciclo: [1, [Validators.required, Validators.min(1), Validators.max(10)]],
    cantidad: [0, [Validators.required, Validators.min(1), Validators.max(20)]],
    filas: this.fb.array([] as ReturnType<MallasComponent['crearFilaCurso']>[]),
  });

  get filasCursos() {
    return this.cursosLoteForm.controls.filas;
  }

  ngOnInit(): void {
    this.cargarFacultades();
  }

  cerrarSesion(): void {
    this.auth.logout();
    void this.router.navigate(['/login']);
  }

  cambiarFacultad(valor: string): void {
    const codigo = Number(valor) || null;
    this.codigoFacultad.set(codigo);
    this.codigoEscuela.set(null);
    this.correlativoPlan.set(null);
    this.escuelas.set([]);
    this.planes.set([]);
    this.cursos.set([]);
    if (codigo) this.cargarEscuelas(codigo);
  }

  cambiarEscuela(valor: string): void {
    const codigo = Number(valor) || null;
    this.codigoEscuela.set(codigo);
    this.correlativoPlan.set(null);
    this.planes.set([]);
    this.cursos.set([]);
    if (codigo && this.codigoFacultad()) this.cargarPlanes(this.codigoFacultad()!, codigo);
  }

  seleccionarPlan(plan: PlanEstudio): void {
    this.correlativoPlan.set(plan.correlativo);
    this.cargarCursos(plan.codigoFacultad, plan.codigoEscuela, plan.correlativo);
  }

  abrirFacultad(): void {
    this.error.set('');
    this.facultadForm.reset({ sigla: '', nombre: '' });
    this.modal.set('facultad');
  }

  abrirEscuela(): void {
    if (!this.codigoFacultad()) return;
    this.error.set('');
    this.escuelaForm.reset({ nombre: '' });
    this.modal.set('escuela');
  }

  abrirPlan(plan?: PlanEstudio): void {
    if (!this.codigoEscuela()) return;
    this.error.set('');
    this.planEditando.set(plan ?? null);
    this.planForm.reset({
      anio: plan?.anio ?? new Date().getFullYear(),
      nombre: plan?.nombre ?? '',
      resolucion: plan?.resolucion ?? '',
      fechaAprobacion: plan?.fechaAprobacion ?? '',
      fechaInicioVigencia: plan?.fechaInicioVigencia ?? '',
      fechaFinVigencia: plan?.fechaFinVigencia ?? '',
      estado: plan?.estado ?? 'BORRADOR',
    });
    this.modal.set('plan');
  }

  abrirCursos(): void {
    if (!this.correlativoPlan()) return;
    this.error.set('');
    this.cursosLoteForm.controls.ciclo.setValue(1);
    this.cursosLoteForm.controls.cantidad.setValue(0);
    this.filasCursos.clear();
    this.modal.set('cursos');
  }

  abrirCurso(curso: Curso): void {
    if (!this.correlativoPlan()) return;
    this.error.set('');
    this.cursoEditando.set(curso);
    this.cursoForm.reset({
      codigoCurso: curso.codigoCurso,
      nombre: curso.nombre,
      semestre: curso.semestre,
      horasTeoria: curso.horasTeoria ?? 0,
      horasPractica: curso.horasPractica ?? 0,
      creditos: Number(curso.creditos),
      tipo: curso.tipo,
    });
    const seleccionados: Record<string, Prerequisito['tipo']> = {};
    curso.prerequisitos.forEach((item) => (seleccionados[item.codigoCurso] = item.tipo));
    this.prerequisitosSeleccionados.set(seleccionados);
    this.busquedaPrerequisitoEdicion.set('');
    this.modal.set('curso');
  }

  cerrarModal(): void {
    if (this.guardando()) return;
    this.modal.set(null);
    this.cursoEditando.set(null);
    this.planEditando.set(null);
  }

  guardarFacultad(): void {
    if (this.facultadForm.invalid) {
      this.facultadForm.markAllAsTouched();
      return;
    }
    const valor = this.facultadForm.getRawValue();
    this.iniciarGuardado();
    this.api.crearFacultad({ sigla: valor.sigla.trim() || null, nombre: valor.nombre.trim(), estado: 'A' })
      .subscribe({
        next: (facultad) => {
          this.finalizarGuardado('Facultad registrada correctamente.');
          this.cargarFacultades(facultad.codigo);
        },
        error: (error) => this.mostrarError(error),
      });
  }

  guardarEscuela(): void {
    const facultad = this.codigoFacultad();
    if (!facultad || this.escuelaForm.invalid) {
      this.escuelaForm.markAllAsTouched();
      return;
    }
    this.iniciarGuardado();
    this.api.crearEscuela(facultad, { nombre: this.escuelaForm.getRawValue().nombre.trim(), estado: 'A' })
      .subscribe({
        next: (escuela) => {
          this.finalizarGuardado('Carrera registrada correctamente.');
          this.cargarEscuelas(facultad, escuela.codigo);
        },
        error: (error) => this.mostrarError(error),
      });
  }

  guardarPlan(): void {
    const facultad = this.codigoFacultad();
    const escuela = this.codigoEscuela();
    if (!facultad || !escuela || this.planForm.invalid) {
      this.planForm.markAllAsTouched();
      return;
    }
    const valor = this.planForm.getRawValue();
    const request: PlanRequest = {
      anio: Number(valor.anio),
      nombre: valor.nombre.trim(),
      resolucion: valor.resolucion.trim() || null,
      fechaAprobacion: valor.fechaAprobacion || null,
      fechaInicioVigencia: valor.fechaInicioVigencia || null,
      fechaFinVigencia: valor.fechaFinVigencia || null,
      estado: valor.estado,
    };
    this.iniciarGuardado();
    const editando = this.planEditando();
    const operacion = editando
      ? this.api.actualizarPlan(facultad, escuela, editando.correlativo, request)
      : this.api.crearPlan(facultad, escuela, request);
    operacion.subscribe({
      next: (plan) => {
        this.finalizarGuardado(editando ? 'Plan actualizado correctamente.' : 'Plan registrado correctamente.');
        this.cargarPlanes(facultad, escuela, plan.correlativo);
      },
      error: (error) => this.mostrarError(error),
    });
  }

  guardarCurso(): void {
    const facultad = this.codigoFacultad();
    const escuela = this.codigoEscuela();
    const plan = this.correlativoPlan();
    if (!facultad || !escuela || !plan || this.cursoForm.invalid) {
      this.cursoForm.markAllAsTouched();
      return;
    }
    const valor = this.cursoForm.getRawValue();
    const request: CursoRequest = {
      codigoCurso: valor.codigoCurso.trim().toUpperCase(),
      nombre: valor.nombre.trim(),
      semestre: Number(valor.semestre),
      horasTeoria: Number(valor.horasTeoria),
      horasPractica: Number(valor.horasPractica),
      creditos: Number(valor.creditos),
      tipo: valor.tipo,
      estado: this.cursoEditando()?.estado ?? 'A',
      prerequisitos: Object.entries(this.prerequisitosSeleccionados()).map(([codigoCurso, tipo]) => ({
        codigoCurso,
        tipo,
      })),
    };
    this.iniciarGuardado();
    const editando = this.cursoEditando();
    const operacion = editando
      ? this.api.actualizarCurso(facultad, escuela, plan, editando.codigoCurso, request)
      : this.api.crearCurso(facultad, escuela, plan, request);
    operacion.subscribe({
      next: () => {
        this.finalizarGuardado(editando ? 'Curso actualizado correctamente.' : 'Curso agregado a la malla.');
        this.cargarCursos(facultad, escuela, plan);
      },
      error: (error) => this.mostrarError(error),
    });
  }

  eliminarCurso(curso: Curso): void {
    const facultad = this.codigoFacultad();
    const escuela = this.codigoEscuela();
    const plan = this.correlativoPlan();
    if (!facultad || !escuela || !plan) return;
    const confirmado = window.confirm(
      `¿Deseas eliminar el curso ${curso.codigoCurso} — ${curso.nombre}?\n\nEsta acción también quitará sus relaciones de prerrequisito.`,
    );
    if (!confirmado) return;

    this.iniciarGuardado();
    this.api.eliminarCurso(facultad, escuela, plan, curso.codigoCurso).subscribe({
      next: () => {
        this.guardando.set(false);
        this.aviso.set(`El curso ${curso.codigoCurso} fue eliminado correctamente.`);
        window.setTimeout(() => this.aviso.set(''), 3500);
        this.cargarCursos(facultad, escuela, plan);
      },
      error: (error) => this.mostrarError(error),
    });
  }

  totalHorasTeoria(cursos: Curso[]): number {
    return this.cursosComputables(cursos)
      .reduce((total, curso) => total + Number(curso.horasTeoria ?? 0), 0);
  }

  totalHorasPractica(cursos: Curso[]): number {
    return this.cursosComputables(cursos)
      .reduce((total, curso) => total + Number(curso.horasPractica ?? 0), 0);
  }

  totalCreditosCiclo(cursos: Curso[]): number {
    return this.cursosComputables(cursos)
      .reduce((total, curso) => total + Number(curso.creditos), 0);
  }

  cantidadCursosCurriculares(cursos: Curso[]): number {
    return this.cursosComputables(cursos).length;
  }

  cantidadAlternativasElectivas(cursos: Curso[]): number {
    return cursos.filter((curso) => !!curso.grupoElectivo).length;
  }

  actualizarCantidad(valor: string): void {
    const cantidad = Math.max(0, Math.min(20, Number(valor) || 0));
    this.cursosLoteForm.controls.cantidad.setValue(cantidad, { emitEvent: false });
    while (this.filasCursos.length < cantidad) this.filasCursos.push(this.crearFilaCurso());
    while (this.filasCursos.length > cantidad) this.filasCursos.removeAt(this.filasCursos.length - 1);
  }

  actualizarCicloLote(): void {
    const ciclo = Number(this.cursosLoteForm.controls.ciclo.value);
    this.filasCursos.controls.forEach((fila) => {
      fila.controls.prerequisitos.setValue(
        fila.controls.prerequisitos.value.filter((codigo) => {
          const curso = this.cursos().find((item) => item.codigoCurso === codigo);
          return curso && curso.semestre < ciclo;
        }),
      );
      fila.controls.busquedaPrerequisito.setValue('');
    });
  }

  guardarCursos(): void {
    const facultad = this.codigoFacultad();
    const escuela = this.codigoEscuela();
    const plan = this.correlativoPlan();
    if (!facultad || !escuela || !plan || this.cursosLoteForm.invalid || this.filasCursos.length === 0) {
      this.cursosLoteForm.markAllAsTouched();
      return;
    }

    const ciclo = Number(this.cursosLoteForm.controls.ciclo.value);
    const requests: CursoRequest[] = this.filasCursos.getRawValue().map((fila) => ({
      codigoCurso: fila.codigoCurso.trim().toUpperCase(),
      nombre: fila.nombre.trim(),
      semestre: ciclo,
      horasTeoria: Number(fila.horasTeoria),
      horasPractica: Number(fila.horasPractica),
      creditos: Number(fila.creditos),
      tipo: fila.tipo,
      estado: 'A',
      prerequisitos: fila.prerequisitos.map((codigoCurso) => ({
        codigoCurso,
        tipo: 'OBLIGATORIO' as const,
      })),
    }));

    this.iniciarGuardado();
    this.api.crearCursos(facultad, escuela, plan, requests).subscribe({
      next: (guardados) => {
        this.finalizarGuardado(`${guardados.length} cursos agregados al ciclo ${ciclo}.`);
        this.cargarCursos(facultad, escuela, plan);
      },
      error: (error) => this.mostrarError(error),
    });
  }

  alternarPrerequisito(codigoCurso: string, marcado: boolean): void {
    const seleccionados = { ...this.prerequisitosSeleccionados() };
    if (marcado) seleccionados[codigoCurso] = 'OBLIGATORIO';
    else delete seleccionados[codigoCurso];
    this.prerequisitosSeleccionados.set(seleccionados);
  }

  cambiarTipoPrerequisito(codigoCurso: string, tipo: string): void {
    this.prerequisitosSeleccionados.update((actuales) => ({
      ...actuales,
      [codigoCurso]: tipo as Prerequisito['tipo'],
    }));
  }

  esPrerequisito(codigoCurso: string): boolean {
    return codigoCurso in this.prerequisitosSeleccionados();
  }

  tipoPrerequisito(codigoCurso: string): Prerequisito['tipo'] {
    return this.prerequisitosSeleccionados()[codigoCurso] ?? 'OBLIGATORIO';
  }

  nombrePrerequisito(codigoCurso: string): string {
    return this.cursos().find((curso) => curso.codigoCurso === codigoCurso)?.nombre ?? codigoCurso;
  }

  opcionesPrerequisitoFila(indice: number): Curso[] {
    const fila = this.filasCursos.at(indice).getRawValue();
    return this.filtrarPrerequisitos(
      fila.busquedaPrerequisito,
      fila.tipo,
      Number(this.cursosLoteForm.controls.ciclo.value),
      fila.prerequisitos,
    );
  }

  prerequisitosDeFila(indice: number): Curso[] {
    const codigos = this.filasCursos.at(indice).controls.prerequisitos.value;
    return codigos
      .map((codigo) => this.cursos().find((curso) => curso.codigoCurso === codigo))
      .filter((curso): curso is Curso => !!curso);
  }

  seleccionarPrerequisitoFila(indice: number, codigoCurso: string): void {
    const fila = this.filasCursos.at(indice).controls;
    if (!fila.prerequisitos.value.includes(codigoCurso)) {
      fila.prerequisitos.setValue([...fila.prerequisitos.value, codigoCurso]);
    }
    fila.busquedaPrerequisito.setValue('');
  }

  quitarPrerequisitoFila(indice: number, codigoCurso: string): void {
    const control = this.filasCursos.at(indice).controls.prerequisitos;
    control.setValue(control.value.filter((codigo) => codigo !== codigoCurso));
  }

  cambiarTipoCursoFila(indice: number): void {
    const fila = this.filasCursos.at(indice).controls;
    fila.prerequisitos.setValue(
      fila.prerequisitos.value.filter((codigo) => {
        const curso = this.cursos().find((item) => item.codigoCurso === codigo);
        return curso && this.tipoCompatible(curso, fila.tipo.value);
      }),
    );
    fila.busquedaPrerequisito.setValue('');
  }

  opcionesPrerequisitoEdicion(): Curso[] {
    return this.filtrarPrerequisitos(
      this.busquedaPrerequisitoEdicion(),
      this.cursoForm.controls.tipo.value,
      Number(this.cursoForm.controls.semestre.value),
      Object.keys(this.prerequisitosSeleccionados()),
      this.cursoEditando()?.codigoCurso,
    );
  }

  seleccionarPrerequisitoEdicion(codigoCurso: string): void {
    this.alternarPrerequisito(codigoCurso, true);
    this.busquedaPrerequisitoEdicion.set('');
  }

  actualizarCompatibilidadPrerequisitosEdicion(): void {
    const tipo = this.cursoForm.controls.tipo.value;
    const ciclo = Number(this.cursoForm.controls.semestre.value);
    const compatibles = Object.fromEntries(
      Object.entries(this.prerequisitosSeleccionados()).filter(([codigo]) => {
        const curso = this.cursos().find((item) => item.codigoCurso === codigo);
        return curso && curso.semestre < ciclo && this.tipoCompatible(curso, tipo);
      }),
    ) as Record<string, Prerequisito['tipo']>;
    this.prerequisitosSeleccionados.set(compatibles);
    this.busquedaPrerequisitoEdicion.set('');
  }

  private cargarFacultades(seleccionar?: number): void {
    this.cargando.set(true);
    this.api.listarFacultades().subscribe({
      next: (items) => {
        this.facultades.set(items);
        this.cargando.set(false);
        if (seleccionar) {
          this.codigoFacultad.set(seleccionar);
          this.cargarEscuelas(seleccionar);
        }
      },
      error: (error) => this.mostrarError(error),
    });
  }

  private cargarEscuelas(facultad: number, seleccionar?: number): void {
    this.cargando.set(true);
    this.api.listarEscuelas(facultad).subscribe({
      next: (items) => {
        this.escuelas.set(items);
        this.cargando.set(false);
        if (seleccionar) {
          this.codigoEscuela.set(seleccionar);
          this.cargarPlanes(facultad, seleccionar);
        }
      },
      error: (error) => this.mostrarError(error),
    });
  }

  private cargarPlanes(facultad: number, escuela: number, seleccionar?: number): void {
    this.cargando.set(true);
    this.api.listarPlanes(facultad, escuela).subscribe({
      next: (items) => {
        this.planes.set(items);
        this.cargando.set(false);
        if (seleccionar) {
          const plan = items.find((item) => item.correlativo === seleccionar);
          if (plan) this.seleccionarPlan(plan);
        }
      },
      error: (error) => this.mostrarError(error),
    });
  }

  private cargarCursos(facultad: number, escuela: number, plan: number): void {
    this.cargando.set(true);
    this.api.listarCursos(facultad, escuela, plan).subscribe({
      next: (items) => {
        this.cursos.set(items);
        this.cargando.set(false);
      },
      error: (error) => this.mostrarError(error),
    });
  }

  private iniciarGuardado(): void {
    this.error.set('');
    this.guardando.set(true);
  }

  private crearFilaCurso() {
    return this.fb.nonNullable.group({
      codigoCurso: ['', [Validators.required, Validators.maxLength(20)]],
      nombre: ['', [Validators.required, Validators.maxLength(180)]],
      horasTeoria: [0, [Validators.required, Validators.min(0), Validators.max(40)]],
      horasPractica: [0, [Validators.required, Validators.min(0), Validators.max(40)]],
      creditos: [1, [Validators.required, Validators.min(1), Validators.max(99), Validators.pattern(/^\d+$/)]],
      tipo: ['OBLIGATORIO' as TipoCurso, [Validators.required]],
      busquedaPrerequisito: [''],
      prerequisitos: this.fb.nonNullable.control<string[]>([]),
    });
  }

  private filtrarPrerequisitos(
    busqueda: string,
    tipoCurso: TipoCurso,
    ciclo: number,
    seleccionados: string[],
    excluir?: string,
  ): Curso[] {
    const termino = this.normalizar(busqueda.trim());
    if (!termino) return [];
    const codigosSeleccionados = new Set(seleccionados);
    return this.cursos()
      .filter((curso) =>
        curso.estado === 'A'
        && curso.codigoCurso !== excluir
        && curso.semestre < ciclo
        && this.tipoCompatible(curso, tipoCurso)
        && !codigosSeleccionados.has(curso.codigoCurso)
        && this.normalizar(`${curso.codigoCurso} ${curso.nombre}`).includes(termino),
      )
      .slice(0, 8);
  }

  private tipoCompatible(candidato: Curso, tipoCurso: TipoCurso): boolean {
    return tipoCurso === 'ELECTIVO' || candidato.tipo !== 'ELECTIVO';
  }

  private cursosComputables(cursos: Curso[]): Curso[] {
    const gruposContados = new Set<string>();
    return cursos.filter((curso) => {
      if (!curso.grupoElectivo) return true;
      if (gruposContados.has(curso.grupoElectivo)) return false;
      gruposContados.add(curso.grupoElectivo);
      return true;
    });
  }

  private normalizar(valor: string): string {
    return valor.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
  }

  private finalizarGuardado(mensaje: string): void {
    this.guardando.set(false);
    this.modal.set(null);
    this.aviso.set(mensaje);
    window.setTimeout(() => this.aviso.set(''), 3500);
  }

  private mostrarError(error: HttpErrorResponse): void {
    this.cargando.set(false);
    this.guardando.set(false);
    if (error.status === 401) {
      this.cerrarSesion();
      return;
    }
    if (error.status === 0) {
      this.error.set('No se pudo conectar con el backend. Revisa que Spring Boot esté iniciado en el puerto 8080.');
    } else {
      this.error.set(error.error?.message ?? 'No se pudo completar la operación.');
    }
  }
}
