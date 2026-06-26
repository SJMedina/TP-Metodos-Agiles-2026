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
  private readonly loginUrl = `${environment.apiUrl.replace('/api', '')}/auth/super/login`;

  login(id: string, password: string): Observable<SuperLoginResponse> {
    return this.http.post<SuperLoginResponse>(
      this.loginUrl,
      { username: id, password }
    ).pipe(
      tap(response => {
        if (response.success && isPlatformBrowser(this.platformId)) {
          localStorage.setItem('super_token', response.token);
          localStorage.setItem('super_id', response.id);
          localStorage.setItem('credentials', JSON.stringify({ username: id, password }));
        }
      })
    );
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('super_token');
      localStorage.removeItem('super_id');
      localStorage.removeItem('credentials');
    }
  }

  isAuthenticated(): boolean {
    if (!isPlatformBrowser(this.platformId)) return false;
    return !!localStorage.getItem('super_token');
  }
}
