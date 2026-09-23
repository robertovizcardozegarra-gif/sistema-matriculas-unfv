import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';
import { LoginComponent } from './pages/login/login.component';
import { MallasComponent } from './pages/mallas/mallas.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'mallas', component: MallasComponent, canActivate: [authGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'mallas' },
  { path: '**', redirectTo: 'mallas' },
];
