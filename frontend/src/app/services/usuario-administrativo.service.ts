import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CrearUsuarioRequest, ActualizarUsuarioRequest, UsuarioAdministrativo } from '../models/usuario-administrativo';

@Injectable({ providedIn: 'root' })
export class UsuarioAdministrativoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/usuarios`;

  crear(data: CrearUsuarioRequest): Observable<UsuarioAdministrativo> {
    return this.http.post<UsuarioAdministrativo>(this.apiUrl, data);
  }

  listar(): Observable<UsuarioAdministrativo[]> {
    return this.http.get<UsuarioAdministrativo[]>(this.apiUrl);
  }

  actualizar(id: string, data: ActualizarUsuarioRequest): Observable<UsuarioAdministrativo> {
    return this.http.put<UsuarioAdministrativo>(`${this.apiUrl}/${id}`, data);
  }
}
