import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LicenciaCostoService {
  // El puerto debe coincidir con el puerto del backend
  private apiUrl = 'http://localhost:8080/api/licencias/costo';

  constructor(private http: HttpClient) { }

  obtenerCosto(clase: string, vigencia: number): Observable<number> {
    const params = new HttpParams()
      .set('clase', clase)
      .set('vigencia', vigencia.toString());

    return this.http.get<number>(this.apiUrl, { params });
  }
}
