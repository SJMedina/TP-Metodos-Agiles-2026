import { environment } from '../environments/environment';
import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject, Observable, catchError, throwError, tap } from 'rxjs';

interface LoginResponse {
  success?: boolean;
  token: string;
  username: string;
  roles?: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private api = `${environment.apiUrl.replace('/api', '')}/auth`;
  private authSubject = new BehaviorSubject<LoginResponse | null>(null);
  public auth$ = this.authSubject.asObservable();
  private platformId = inject(PLATFORM_ID);

  constructor(private http: HttpClient) {
    if (isPlatformBrowser(this.platformId)) {
      this.loadUserFromStorage();
    }
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.api}/login`, { username, password }).pipe(
      tap(response => {
        if (response && response.token && isPlatformBrowser(this.platformId)) {
          localStorage.setItem('user', JSON.stringify(response));
          localStorage.setItem('token', response.token);
          localStorage.setItem('credentials', JSON.stringify({ username, password }));
          this.authSubject.next(response);
        }
      }),
      catchError(error => this.handleError(error))
    );
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      localStorage.removeItem('credentials');
    }
    this.authSubject.next(null);
  }

  isAuthenticated(): boolean {
    if (!isPlatformBrowser(this.platformId)) {
      return false;
    }

    const hasToken = !!localStorage.getItem('token');
    const hasCredentials = !!localStorage.getItem('credentials');

    if (hasToken && hasCredentials) {
      return true;
    }

    if (hasToken || hasCredentials) {
      this.logout();
    }

    return false;
  }

  getUser(): LoginResponse | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }
    const userJson = localStorage.getItem('user');
    return userJson ? JSON.parse(userJson) : null;
  }

  getBasicAuthHeader(): string | null {
    if (!isPlatformBrowser(this.platformId)) {
      return null;
    }
    const credentialsJson = localStorage.getItem('credentials');
    if (!credentialsJson) {
      return null;
    }
    try {
      const creds = JSON.parse(credentialsJson);
      if (!creds?.username || !creds?.password) {
        this.logout();
        return null;
      }
      const credentials = `${creds.username}:${creds.password}`;
      return 'Basic ' + btoa(credentials);
    } catch (e) {
      this.logout();
      return null;
    }
  }

  private loadUserFromStorage(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }
    const userJson = localStorage.getItem('user');
    if (!this.isAuthenticated()) {
      return;
    }
    if (userJson) {
      this.authSubject.next(JSON.parse(userJson));
    }
  }

  private handleError(error: HttpErrorResponse) {
    return throwError(() => error);
  }
}
