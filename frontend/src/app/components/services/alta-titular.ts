import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Titular } from '../models/titular';

@Injectable({
  providedIn: 'root'
})
export class TitularService {
  // Ajusta el puerto si tu Spring Boot corre en uno distinto
  private apiUrl = 'http://localhost:8080/api/titulares';

  constructor(private http: HttpClient) { }

  registrarTitular(titular: Titular): Observable<Titular> {
    return this.http.post<Titular>(this.apiUrl, titular);
  }

  modificarTitular(id: number, titular: Titular): Observable<Titular> {
    return this.http.put<Titular>(`${this.apiUrl}/${id}`, titular);
  }

  buscarPorId(id: number): Observable<Titular> {
    return this.http.get<Titular>(`${this.apiUrl}/${id}`);
  }
}
