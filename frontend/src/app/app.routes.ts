import { Routes } from '@angular/router';
import { ListaLicenciasComponent } from './components/lista-licencias/lista-licencias.component';
import { EmitirLicenciasComponent } from './components/emitir-licencias/emitir-licencias.component';
import { LoginComponent } from './components/login/login.component';
import { authGuard } from './auth.guard';
import { RenovarLicenciaComponent } from './components/renovar-licencia/renovar-licencia.component';
import { RenovarVencimientoComponent } from './components/renovar-licencia/vencimiento/renovar-vencimiento.component';
import { RenovarModificacionComponent } from './components/renovar-licencia/modifacion/renovar-modificacion.component';
import { ListadoExpiradasComponent } from './components/listado-expiradas/listado-expiradas.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'listar-licencias', component: ListaLicenciasComponent, canActivate: [authGuard] },
  { path: 'emitir-licencias', component: EmitirLicenciasComponent, canActivate: [authGuard] },
  { path: 'renovar-licencia', component: RenovarLicenciaComponent, canActivate: [authGuard] },
  { path: 'renovar-licencia/vencimiento', component: RenovarVencimientoComponent, canActivate: [authGuard] },
  { path: 'renovar-licencia/modificacion', component: RenovarModificacionComponent, canActivate: [authGuard] },
  { path: 'licencias-expiradas', component: ListadoExpiradasComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' }
];
