import { Routes } from '@angular/router';
import { AltaTitularComponent } from './components/alta-titular/alta-titular';
import { ModificarTitularComponent } from './components/modificar-titular/modificar-titular';
import { ListaLicenciasComponent } from './components/lista-licencias/lista-licencias.component';
import { EmitirLicenciasComponent } from './components/emitir-licencias/emitir-licencias.component';
import { LoginComponent } from './components/login/login.component';
import { authGuard } from './auth.guard';
import { RenovarLicenciaComponent } from './components/renovar-licencia/renovar-licencia.component';
import { RenovarVencimientoComponent } from './components/renovar-licencia/vencimiento/renovar-vencimiento.component';
import { RenovarModificacionComponent } from './components/renovar-licencia/modifacion/renovar-modificacion.component';
import { ListaLicenciasVigentesComponent } from './components/lista-licencias-vigentes/lista-licencias-vigentes.component';
import { AltaUsuarioComponent } from './components/alta-usuario/alta-usuario.component';
import { LoginSuperUsuarioComponent } from './components/login-super-usuario/login-super-usuario.component';
import { ModificarUsuarioComponent } from './components/modificar-usuario/modificar-usuario.component';
import { superUsuarioGuard } from './guards/super-usuario.guard';
import { ListadoExpiradasComponent } from './components/listado-expiradas/listado-expiradas.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'listar-licencias', component: ListaLicenciasComponent, canActivate: [authGuard] },
  { path: 'emitir-licencias', component: EmitirLicenciasComponent, canActivate: [authGuard] },
  { path: 'renovar-licencia', component: RenovarLicenciaComponent, canActivate: [authGuard] },
  { path: 'renovar-licencia/vencimiento', component: RenovarVencimientoComponent, canActivate: [authGuard] },
  { path: 'renovar-licencia/modificacion', component: RenovarModificacionComponent, canActivate: [authGuard] },
  { path: 'licencias-vigentes', component: ListaLicenciasVigentesComponent, canActivate: [authGuard] },
  { path: 'login-super', component: LoginSuperUsuarioComponent },
  { path: 'alta-usuario', component: AltaUsuarioComponent, canActivate: [superUsuarioGuard] },
  { path: 'licencias-expiradas', component: ListadoExpiradasComponent, canActivate: [authGuard] },
  { path: 'modificar-usuario', component: ModificarUsuarioComponent, canActivate: [authGuard] },
  { path: 'alta', component: AltaTitularComponent },
  { path: 'modificar-titular/:id', component: ModificarTitularComponent },
  { path: '**', redirectTo: 'login' }
];
