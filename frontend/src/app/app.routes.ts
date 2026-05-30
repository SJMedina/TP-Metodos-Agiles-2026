import { Routes } from '@angular/router';
import { ListaLicenciasComponent } from './components/lista-licencias/lista-licencias.component';
import { EmitirLicenciasComponent } from './components/emitir-licencias/emitir-licencias.component';
import { LoginComponent } from './components/login/login.component';
import { authGuard } from './auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'listar-licencias', component: ListaLicenciasComponent, canActivate: [authGuard] },
  { path: 'emitir-licencias', component: EmitirLicenciasComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' }
];
