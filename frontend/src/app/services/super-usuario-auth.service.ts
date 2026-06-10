import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

interface SuperLoginResponse {
  success: boolean;
  id: string;
  token: string;
}

@Injectable({ providedIn: 'root' })
export class SuperUsuarioAuthService {
  private readonly http = inject(HttpClient);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly apiUrl = `${environment.apiUrl}/auth/super/login`.replace('/api', '');

  login(id: string, password: string): Observable<SuperLoginResponse> {
    return this.http.post<SuperLoginResponse>(
      `${environment.apiUrl.replace('/api', '')}/auth/super/login`,
      { username: id, password }
    ).pipe(
      tap(response => {
        if (response.success && isPlatformBrowser(this.platformId)) {
          localStorage.setItem('super_token', response.token);
          localStorage.setItem('super_id', response.id);
        }
      })
    );
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('super_token');
      localStorage.removeItem('super_id');
    }
  }

  isAuthenticated(): boolean {
    if (!isPlatformBrowser(this.platformId)) return false;
    return !!localStorage.getItem('super_token');
  }
}
