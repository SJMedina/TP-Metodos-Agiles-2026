import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Licencia } from '../models/licencia';

@Injectable({providedIn:'root'})
export class LicenciaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/licencias`;

  listarLicencias(): Observable<Licencia[]> {
    return this.http.get<Licencia[]>(this.apiUrl);
  }

  emitirLicencia(data: Partial<Licencia>): Observable<Licencia> {
    return this.http.post<Licencia>(this.apiUrl, data);
  }
}
