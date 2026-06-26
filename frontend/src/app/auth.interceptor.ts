import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const authorizationHeader = this.authService.getBasicAuthHeader();

    if (!authorizationHeader) {
      return next.handle(req);
    }

    const authenticatedRequest = req.clone({
      headers: req.headers.set('Authorization', authorizationHeader)
    });

    return next.handle(authenticatedRequest);
  }
}
