import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { LicenciaService } from './licencia.service';

describe('LicenciaService - listarExpiradas', () => {
  let service: LicenciaService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LicenciaService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(LicenciaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería hacer GET a /expiradas sin parámetros cuando no se pasan fechas', () => {
    service.listarExpiradas().subscribe(licencias => {
      expect(licencias).toEqual([]);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/licencias/expiradas');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.keys().length).toBe(0);
    req.flush([]);
  });

  it('debería incluir ambos parámetros de fecha en la URL cuando se proporcionan', () => {
    service.listarExpiradas('2020-01-01', '2023-12-31').subscribe();

    const req = httpMock.expectOne(
      'http://localhost:8080/api/licencias/expiradas?desde=2020-01-01&hasta=2023-12-31'
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('desde')).toBe('2020-01-01');
    expect(req.request.params.get('hasta')).toBe('2023-12-31');
    req.flush([]);
  });

  it('debería incluir solo el parámetro desde cuando solo se proporciona fecha inicio', () => {
    service.listarExpiradas('2021-01-01').subscribe();

    const req = httpMock.expectOne(
      'http://localhost:8080/api/licencias/expiradas?desde=2021-01-01'
    );
    expect(req.request.params.get('desde')).toBe('2021-01-01');
    expect(req.request.params.get('hasta')).toBeNull();
    req.flush([]);
  });

  it('debería incluir solo el parámetro hasta cuando solo se proporciona fecha fin', () => {
    service.listarExpiradas(undefined, '2023-06-30').subscribe();

    const req = httpMock.expectOne(
      'http://localhost:8080/api/licencias/expiradas?hasta=2023-06-30'
    );
    expect(req.request.params.get('hasta')).toBe('2023-06-30');
    expect(req.request.params.get('desde')).toBeNull();
    req.flush([]);
  });
});
