import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginResponse } from './models';

export const TOKEN_KEY = 'unfv_matriculas_token';
const USER_KEY = 'unfv_matriculas_usuario';
const EXPIRATION_KEY = 'unfv_matriculas_expiracion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  readonly usuario = signal(sessionStorage.getItem(USER_KEY) ?? 'admin');

  login(usuario: string, clave: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/login`, { usuario, clave })
      .pipe(
        tap((response) => {
          sessionStorage.setItem(TOKEN_KEY, response.token);
          sessionStorage.setItem(USER_KEY, response.usuario);
          sessionStorage.setItem(EXPIRATION_KEY, response.expiraEn);
          this.usuario.set(response.usuario);
        }),
      );
  }

  tieneSesion(): boolean {
    const token = sessionStorage.getItem(TOKEN_KEY);
    const expiraEn = sessionStorage.getItem(EXPIRATION_KEY);
    if (!token || !expiraEn || new Date(expiraEn).getTime() <= Date.now()) {
      this.logout();
      return false;
    }
    return true;
  }

  logout(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
    sessionStorage.removeItem(EXPIRATION_KEY);
    this.usuario.set('admin');
  }
}
