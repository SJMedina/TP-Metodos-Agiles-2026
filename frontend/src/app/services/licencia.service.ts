import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Licencia } from '../models/licencia';

@Injectable({ providedIn: 'root' })
export class LicenciaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/licencias`;

  listarLicencias(): Observable<Licencia[]> {
    return this.http.get<Licencia[]>(this.apiUrl);
  }

  emitirLicencia(data: Partial<Licencia>): Observable<Licencia> {
    return this.http.post<Licencia>(this.apiUrl, data);
  }
  renovarLicencia(payload: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/renovar`, payload);
  }

  listarPorDocumento(documento: string): Observable<Licencia[]> {
    return this.http.get<Licencia[]>(`${this.apiUrl}/${documento}`);
  }

  listarExpiradas(desde?: string, hasta?: string): Observable<Licencia[]> {
    let params = new HttpParams();
    if (desde) params = params.set('desde', desde);
    if (hasta) params = params.set('hasta', hasta);
    return this.http.get<Licencia[]>(`${this.apiUrl}/expiradas`, { params });
  }
}
