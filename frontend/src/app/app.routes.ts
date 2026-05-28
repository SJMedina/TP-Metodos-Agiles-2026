import { Routes } from '@angular/router';
import { AltaTitularComponent } from './components/alta-titular/alta-titular';

export const routes: Routes = [
  // 1. Cuando entres a localhost:4200 (ruta vacía), te enviará automáticamente a /alta
  { path: '', redirectTo: '/alta', pathMatch: 'full' },
  
  // 2. Cuando entres a /alta, cargará el formulario
  { path: 'alta', component: AltaTitularComponent }
];