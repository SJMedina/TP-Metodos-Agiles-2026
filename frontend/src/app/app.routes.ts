import { Routes } from '@angular/router';
import { ListaLicenciasComponent } from './components/lista-licencias/lista-licencias.component';
import { EmitirLicenciasComponent } from './components/emitir-licencias/emitir-licencias.component';
import { LoginComponent } from './components/login/login.component';
import { authGuard } from './auth.guard';
import { RenovarLicenciaComponent } from './components/renovar-licencia/renovar-licencia.component';
import { RenovarVencimientoComponent } from './components/renovar-licencia/vencimiento/renovar-vencimiento.component';
import { RenovarModificacionComponent } from './components/renovar-licencia/modifacion/renovar-modificacion.component';
import { AltaUsuarioComponent } from './components/alta-usuario/alta-usuario.component';
import { LoginSuperUsuarioComponent } from './components/login-super-usuario/login-super-usuario.component';
import { superUsuarioGuard } from './guards/super-usuario.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'listar-licencias', component: ListaLicenciasComponent, canActivate: [authGuard] },
  { path: 'emitir-licencias', component: EmitirLicenciasComponent, canActivate: [authGuard] },
  { path: 'renovar-licencia', component: RenovarLicenciaComponent, canActivate: [authGuard] },
  { path: 'renovar-licencia/vencimiento', component: RenovarVencimientoComponent, canActivate: [authGuard] },
  { path: 'renovar-licencia/modificacion', component: RenovarModificacionComponent, canActivate: [authGuard] },
  { path: 'login-super', component: LoginSuperUsuarioComponent },
  { path: 'alta-usuario', component: AltaUsuarioComponent, canActivate: [superUsuarioGuard] },
  { path: '**', redirectTo: 'login' }
];
