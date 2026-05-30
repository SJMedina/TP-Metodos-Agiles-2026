import { environment } from '../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth.service';

@Injectable({
  providedIn: 'root'
})
export class LicenciaCostoService {
  private apiUrl = `${environment.apiUrl}/licencias/costo`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  obtenerCosto(clase: string, vigencia: number): Observable<number> {
    const params = new HttpParams()
      .set('clase', clase)
      .set('vigencia', vigencia.toString());

    return this.http.get<number>(this.apiUrl, {
      params,
      headers: this.getHeaders()
    });
  }

  private getHeaders(): HttpHeaders {
    let headers = new HttpHeaders();
    const basicAuth = this.authService.getBasicAuthHeader();
    if (basicAuth) {
      headers = headers.set('Authorization', basicAuth);
    }
    return headers;
  }
}
