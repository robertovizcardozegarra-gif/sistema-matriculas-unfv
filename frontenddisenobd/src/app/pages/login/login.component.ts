import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly enviando = signal(false);
  readonly mostrarClave = signal(false);
  readonly error = signal('');

  readonly form = this.fb.nonNullable.group({
    usuario: ['admin', [Validators.required]],
    clave: ['', [Validators.required]],
  });

  constructor() {
    if (this.auth.tieneSesion()) {
      void this.router.navigate(['/mallas']);
    }
  }

  ingresar(): void {
    if (this.form.invalid || this.enviando()) {
      this.form.markAllAsTouched();
      return;
    }

    this.error.set('');
    this.enviando.set(true);
    const { usuario, clave } = this.form.getRawValue();
    this.auth.login(usuario.trim(), clave).subscribe({
      next: () => void this.router.navigate(['/mallas']),
      error: (error: HttpErrorResponse) => {
        this.enviando.set(false);
        if (error.status === 0) {
          this.error.set('No se pudo conectar con el backend. Comprueba que Spring Boot esté ejecutándose.');
        } else if (error.status === 401) {
          this.error.set('El usuario o la contraseña no son correctos.');
        } else {
          this.error.set(error.error?.message ?? 'No se pudo iniciar sesión. Inténtalo nuevamente.');
        }
      },
    });
  }
}
