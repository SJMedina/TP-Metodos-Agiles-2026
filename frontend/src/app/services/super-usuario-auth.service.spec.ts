import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { SuperUsuarioAuthService } from './super-usuario-auth.service';

describe('SuperUsuarioAuthService', () => {
  let service: SuperUsuarioAuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        SuperUsuarioAuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(SuperUsuarioAuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('debería crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('login exitoso debería guardar el token en localStorage', () => {
    const mockResponse = { success: true, id: 'superadmin', token: 'super:123:superadmin' };

    service.login('superadmin', 'super1234').subscribe(res => {
      expect(res.success).toBeTrue();
      expect(localStorage.getItem('super_token')).toBe('super:123:superadmin');
      expect(localStorage.getItem('super_id')).toBe('superadmin');
    });

    const req = httpMock.expectOne('http://localhost:8080/auth/super/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ username: 'superadmin', password: 'super1234' });
    req.flush(mockResponse);
  });

  it('login fallido no debería guardar nada en localStorage', () => {
    service.login('superadmin', 'wrong').subscribe({
      error: () => {
        expect(localStorage.getItem('super_token')).toBeNull();
      }
    });

    const req = httpMock.expectOne('http://localhost:8080/auth/super/login');
    req.flush({ success: false, message: 'Credenciales incorrectas' }, { status: 401, statusText: 'Unauthorized' });
  });

  it('isAuthenticated debería devolver true cuando hay token en localStorage', () => {
    localStorage.setItem('super_token', 'super:123:superadmin');
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('isAuthenticated debería devolver false cuando no hay token', () => {
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('logout debería eliminar el token y el id del localStorage', () => {
    localStorage.setItem('super_token', 'super:123:superadmin');
    localStorage.setItem('super_id', 'superadmin');

    service.logout();

    expect(localStorage.getItem('super_token')).toBeNull();
    expect(localStorage.getItem('super_id')).toBeNull();
  });
});
